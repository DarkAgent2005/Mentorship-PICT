package com.pict.mentorship.service;

import com.pict.mentorship.dto.AuthRegisterRequest;
import com.pict.mentorship.dto.AuthResponse;
import com.pict.mentorship.dto.LoginRequest;
import com.pict.mentorship.entity.UserProfile;
import com.pict.mentorship.repository.UserProfileRepository;
import java.util.Locale;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        userProfileRepository.findByEmailIgnoreCase(normalizedEmail)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already exists: " + normalizedEmail);
                });

        UserProfile profile = new UserProfile();
        profile.setFullName(request.getFullName().trim());
        profile.setEmail(normalizedEmail);
        profile.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        profile.setRole(request.getRole());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setBio(request.getBio());
        profile.setSkills(request.getSkills());
        profile.setInterests(request.getInterests());
        profile.setAvailableForMentorship(request.isAvailableForMentorship());

        UserProfile saved = userProfileRepository.save(profile);
        return toAuthResponse("Registration successful", saved);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        UserProfile profile = userProfileRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (profile.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), profile.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!profile.getRole().equals(request.getRole())) {
            throw new IllegalArgumentException("Role mismatch for this user");
        }

        return toAuthResponse("Login successful", profile);
    }

    @Transactional
    public void ensureDemoCredential(String email, String plainPassword) {
        String normalizedEmail = normalizeEmail(email);
        userProfileRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(profile -> {
            String stored = profile.getPasswordHash();
            if (stored == null || !passwordEncoder.matches(plainPassword, stored)) {
                profile.setPasswordHash(passwordEncoder.encode(plainPassword));
                userProfileRepository.save(profile);
            }
        });
    }

    private String normalizeEmail(String email) {
        String safeEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (safeEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        return safeEmail;
    }

    private AuthResponse toAuthResponse(String message, UserProfile profile) {
        AuthResponse response = new AuthResponse();
        response.setMessage(message);
        response.setId(profile.getId());
        response.setFullName(profile.getFullName());
        response.setEmail(profile.getEmail());
        response.setRole(profile.getRole());
        return response;
    }
}
