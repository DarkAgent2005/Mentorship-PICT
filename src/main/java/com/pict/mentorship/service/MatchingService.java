package com.pict.mentorship.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pict.mentorship.dto.MentorMatchResponse;
import com.pict.mentorship.entity.Role;
import com.pict.mentorship.entity.UserProfile;
import com.pict.mentorship.exception.ResourceNotFoundException;
import com.pict.mentorship.repository.UserProfileRepository;

@Service
public class MatchingService {

    private final UserProfileRepository userProfileRepository;

    public MatchingService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<MentorMatchResponse> findMatchesForMentee(Long menteeId, int limit, String preferredSkill) {
        Long safeMenteeId = Objects.requireNonNull(menteeId, "menteeId must not be null");

        UserProfile mentee = userProfileRepository.findById(safeMenteeId)
            .orElseThrow(() -> new ResourceNotFoundException("Mentee not found with id: " + safeMenteeId));

        if (mentee.getRole() != Role.MENTEE) {
            throw new IllegalArgumentException("Provided user is not a mentee");
        }

        java.util.List<UserProfile> mentors = userProfileRepository.findByRoleAndAvailableForMentorshipTrue(Role.MENTOR);
        java.util.Set<String> menteeSkillSignals = union(mentee.getSkills(), mentee.getInterests());
        String normalizedPreferredSkill = normalizeSkill(preferredSkill);

        java.util.List<MentorMatchResponse> matches = new java.util.ArrayList<>();
        for (UserProfile mentor : mentors) {
            java.util.Set<String> mentorSkills = normalizeSet(mentor.getSkills());
            boolean hasPreferredSkill = normalizedPreferredSkill == null
                    || containsPreferredSkill(mentor, normalizedPreferredSkill);

            if (!hasPreferredSkill) {
                continue;
            }

            java.util.Set<String> commonSkills = intersection(menteeSkillSignals, mentorSkills);
            java.util.Set<String> commonInterests = intersection(mentee.getInterests(), mentor.getInterests());

            int score = (commonSkills.size() * 4)
                    + (commonInterests.size() * 3)
                    + (normalizedPreferredSkill == null ? 0 : 8)
                    + Math.max(0, mentor.getYearsOfExperience() / 2);

            if (score > 0) {
                MentorMatchResponse response = new MentorMatchResponse();
                response.setMentorId(mentor.getId());
                response.setMentorName(mentor.getFullName());
                response.setMentorEmail(mentor.getEmail());
                response.setMentorExperience(mentor.getYearsOfExperience());
                response.setMatchedSkills(commonSkills);
                response.setMatchedInterests(commonInterests);
                response.setMatchScore(score);
                matches.add(response);
            }
        }

        matches.sort(java.util.Comparator.comparing(MentorMatchResponse::getMatchScore).reversed());
        if (limit > 0 && matches.size() > limit) {
            return matches.subList(0, limit);
        }
        return matches;
    }

    private java.util.Set<String> intersection(java.util.Set<String> source, java.util.Set<String> target) {
        java.util.Set<String> sourceNormalized = normalizeSet(source);
        java.util.Set<String> targetNormalized = normalizeSet(target);
        sourceNormalized.retainAll(targetNormalized);
        return sourceNormalized;
    }

    private java.util.Set<String> union(java.util.Set<String> first, java.util.Set<String> second) {
        java.util.Set<String> combined = normalizeSet(first);
        combined.addAll(normalizeSet(second));
        return combined;
    }

    private String normalizeSkill(String skill) {
        if (skill == null || skill.isBlank()) {
            return null;
        }
        return skill.trim().toLowerCase();
    }

    private boolean containsPreferredSkill(UserProfile mentor, String preferredSkill) {
        java.util.Set<String> mentorSkills = normalizeSet(mentor.getSkills());
        java.util.Set<String> mentorInterests = normalizeSet(mentor.getInterests());

        for (String value : mentorSkills) {
            if (value.contains(preferredSkill) || preferredSkill.contains(value)) {
                return true;
            }
        }
        for (String value : mentorInterests) {
            if (value.contains(preferredSkill) || preferredSkill.contains(value)) {
                return true;
            }
        }
        return false;
    }

    private java.util.Set<String> normalizeSet(java.util.Set<String> input) {
        java.util.Set<String> normalized = new java.util.HashSet<>();
        if (input == null) {
            return normalized;
        }
        for (String value : input) {
            if (value != null && !value.isBlank()) {
                normalized.add(value.trim().toLowerCase());
            }
        }
        return normalized;
    }
}
