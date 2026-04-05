package com.pict.mentorship.repository;

import com.pict.mentorship.entity.MentorshipSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorshipSessionRepository extends JpaRepository<MentorshipSession, Long> {
    List<MentorshipSession> findByMentorIdOrMenteeIdOrderByScheduledAtAsc(Long mentorId, Long menteeId);
}
