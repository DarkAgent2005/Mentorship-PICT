package com.pict.mentorship.controller;

import com.pict.mentorship.dto.SessionRequest;
import com.pict.mentorship.entity.MentorshipSession;
import com.pict.mentorship.entity.SessionStatus;
import com.pict.mentorship.service.SessionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipSession create(@Valid @RequestBody SessionRequest request) {
        return sessionService.create(request);
    }

    @GetMapping("/user/{userId}")
    public List<MentorshipSession> listByUser(@PathVariable Long userId) {
        return sessionService.listByUser(userId);
    }

    @PatchMapping("/{sessionId}/status")
    public MentorshipSession updateStatus(
            @PathVariable Long sessionId,
            @RequestParam SessionStatus status) {
        return sessionService.updateStatus(sessionId, status);
    }
}
