package com.pict.mentorship.dto;

import java.util.Set;

public class MentorMatchResponse {
    private Long mentorId;
    private String mentorName;
    private String mentorEmail;
    private Integer mentorExperience;
    private Set<String> matchedSkills;
    private Set<String> matchedInterests;
    private Integer matchScore;

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public Integer getMentorExperience() {
        return mentorExperience;
    }

    public void setMentorExperience(Integer mentorExperience) {
        this.mentorExperience = mentorExperience;
    }

    public Set<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(Set<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public Set<String> getMatchedInterests() {
        return matchedInterests;
    }

    public void setMatchedInterests(Set<String> matchedInterests) {
        this.matchedInterests = matchedInterests;
    }

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }
}
