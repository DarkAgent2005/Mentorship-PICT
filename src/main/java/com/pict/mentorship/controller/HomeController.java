package com.pict.mentorship.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/health")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Mentorship Platform API is running");
        response.put("docsHint", "Open / for UI, use /api/profiles, /api/matches, /api/sessions");
        return response;
    }
}
