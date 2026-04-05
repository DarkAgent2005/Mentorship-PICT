package com.pict.mentorship.controller;

import com.pict.mentorship.dto.UserProfileRequest;
import com.pict.mentorship.entity.Role;
import com.pict.mentorship.entity.UserProfile;
import com.pict.mentorship.service.UserProfileService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final UserProfileService userProfileService;

    public ProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfile create(@Valid @RequestBody UserProfileRequest request) {
        return userProfileService.create(request);
    }

    @GetMapping
    public List<UserProfile> getAll(@RequestParam(required = false) Role role) {
        return userProfileService.getAll(role);
    }

    @GetMapping("/{id}")
    public UserProfile getById(@PathVariable Long id) {
        return userProfileService.getById(id);
    }

    @GetMapping("/by-email")
    public UserProfile getByEmail(@RequestParam String email) {
        return userProfileService.getByEmail(email);
    }

    @PutMapping("/{id}")
    public UserProfile update(@PathVariable Long id, @Valid @RequestBody UserProfileRequest request) {
        return userProfileService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userProfileService.delete(id);
    }
}
