package com.ibm.event_sync.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.event_sync.entity.Feedback;

public class EventSummary {
    @JsonProperty
    private Long id;

    @JsonProperty
    private String title;

    @JsonProperty
    private String description;

    @JsonProperty
    private long feedbackCount;

    @JsonProperty
    private List<Feedback> feedback;

    public EventSummary(Long id, String title, String description, long feedbackCount, List<Feedback> feedback) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.feedbackCount = feedbackCount;
        this.feedback = feedback;
    }
}
