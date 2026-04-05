package com.pict.mentorship.service;

import com.pict.mentorship.dto.UserProfileRequest;
import com.pict.mentorship.entity.Role;
import com.pict.mentorship.entity.UserProfile;
import com.pict.mentorship.exception.ResourceNotFoundException;
import com.pict.mentorship.repository.UserProfileRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfile create(UserProfileRequest request) {
        userProfileRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already exists: " + request.getEmail());
                });

        UserProfile profile = new UserProfile();
        mapRequestToEntity(request, profile);
        return userProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<UserProfile> getAll(Role role) {
        if (role == null) {
            return userProfileRepository.findAll();
        }
        return userProfileRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public UserProfile getById(Long id) {
        Long safeId = Objects.requireNonNull(id, "id must not be null");
        return userProfileRepository.findById(safeId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + safeId));
    }

    @Transactional(readOnly = true)
    public UserProfile getByEmail(String email) {
        String safeEmail = Objects.requireNonNull(email, "email must not be null").trim().toLowerCase();
        if (safeEmail.isEmpty()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        return userProfileRepository.findByEmailIgnoreCase(safeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + safeEmail));
    }

    @Transactional
    public UserProfile update(Long id, UserProfileRequest request) {
        UserProfile existing = Objects.requireNonNull(getById(id), "User must exist");

        userProfileRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(profile -> !profile.getId().equals(id))
                .ifPresent(profile -> {
                    throw new IllegalArgumentException("Email already exists: " + request.getEmail());
                });

        mapRequestToEntity(request, existing);
        return userProfileRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        UserProfile existing = Objects.requireNonNull(getById(id), "Profile to delete should not be null");
        userProfileRepository.delete(existing);
    }

    private void mapRequestToEntity(UserProfileRequest request, UserProfile profile) {
        profile.setFullName(request.getFullName().trim());
        profile.setEmail(request.getEmail().trim().toLowerCase());
        profile.setRole(request.getRole());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setBio(request.getBio());
        profile.setSkills(request.getSkills());
        profile.setInterests(request.getInterests());
        profile.setAvailableForMentorship(request.isAvailableForMentorship());
    }
}
