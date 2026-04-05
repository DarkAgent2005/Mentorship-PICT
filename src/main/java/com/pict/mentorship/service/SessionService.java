package com.pict.mentorship.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pict.mentorship.dto.SessionRequest;
import com.pict.mentorship.entity.Role;
import com.pict.mentorship.exception.ResourceNotFoundException;
import com.pict.mentorship.repository.MentorshipSessionRepository;
import com.pict.mentorship.repository.UserProfileRepository;

@Service
public class SessionService {

    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final UserProfileRepository userProfileRepository;

    public SessionService(
            MentorshipSessionRepository mentorshipSessionRepository,
            UserProfileRepository userProfileRepository) {
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public com.pict.mentorship.entity.MentorshipSession create(SessionRequest request) {
        Long mentorId = Objects.requireNonNull(request.getMentorId(), "mentorId must not be null");
        Long menteeId = Objects.requireNonNull(request.getMenteeId(), "menteeId must not be null");

        com.pict.mentorship.entity.UserProfile mentor = userProfileRepository.findById(mentorId)
            .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + mentorId));
        com.pict.mentorship.entity.UserProfile mentee = userProfileRepository.findById(menteeId)
            .orElseThrow(() -> new ResourceNotFoundException("Mentee not found with id: " + menteeId));

        if (mentor.getRole() != Role.MENTOR) {
            throw new IllegalArgumentException("Provided mentorId belongs to a non-mentor user");
        }
        if (mentee.getRole() != Role.MENTEE) {
            throw new IllegalArgumentException("Provided menteeId belongs to a non-mentee user");
        }
        if (!mentor.isAvailableForMentorship()) {
            throw new IllegalArgumentException("Selected mentor is not available for mentorship");
        }

        com.pict.mentorship.entity.MentorshipSession session = new com.pict.mentorship.entity.MentorshipSession();
        session.setMentor(mentor);
        session.setMentee(mentee);
        session.setTopic(request.getTopic().trim());
        session.setScheduledAt(request.getScheduledAt());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setStatus(com.pict.mentorship.entity.SessionStatus.REQUESTED);

        return mentorshipSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.pict.mentorship.entity.MentorshipSession> listByUser(Long userId) {
        Long safeUserId = Objects.requireNonNull(userId, "userId must not be null");

        userProfileRepository.findById(safeUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + safeUserId));
        return mentorshipSessionRepository.findByMentorIdOrMenteeIdOrderByScheduledAtAsc(safeUserId, safeUserId);
    }

    @Transactional
    public com.pict.mentorship.entity.MentorshipSession updateStatus(Long sessionId, com.pict.mentorship.entity.SessionStatus status) {
        Long safeSessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");

        com.pict.mentorship.entity.MentorshipSession session = mentorshipSessionRepository.findById(safeSessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + safeSessionId));
        session.setStatus(status);
        return mentorshipSessionRepository.save(session);
    }
}
