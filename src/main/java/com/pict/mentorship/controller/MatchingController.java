package com.pict.mentorship.controller;

import com.pict.mentorship.dto.MentorMatchResponse;
import com.pict.mentorship.service.MatchingService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/mentee/{menteeId}")
    public List<MentorMatchResponse> getMatches(
            @PathVariable Long menteeId,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String skill) {
        return matchingService.findMatchesForMentee(menteeId, limit, skill);
    }
}
