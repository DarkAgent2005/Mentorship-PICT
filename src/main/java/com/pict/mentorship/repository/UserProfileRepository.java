package com.pict.mentorship.repository;

import com.pict.mentorship.entity.Role;
import com.pict.mentorship.entity.UserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmailIgnoreCase(String email);

    List<UserProfile> findByRole(Role role);

    List<UserProfile> findByRoleAndAvailableForMentorshipTrue(Role role);
}
