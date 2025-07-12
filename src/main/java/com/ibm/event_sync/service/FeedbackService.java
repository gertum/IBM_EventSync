package com.ibm.event_sync.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.ibm.event_sync.entity.Event;
import com.ibm.event_sync.entity.Feedback;
import com.ibm.event_sync.repository.EventRepository;
import com.ibm.event_sync.repository.FeedbackRepository;
import org.springframework.scheduling.annotation.Async;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;

    private final String HUGGINGFACE_API_URL = "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment";

    @Value("${hf.api.key}")
    private String API_KEY;

    public FeedbackService(FeedbackRepository feedbackRepository, EventRepository eventRepository) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * A function for adding feedback to the repository.
     * 
     * @param eventId
     * @param feedback
     * @return
     */
    public Feedback addFeedback(Long eventId, Feedback feedback) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        feedback.setEvent(event);

        // Analyze sentiment
        String sentiment = analyzeSentiment(feedback.getText());
        feedback.setSentiment(sentiment);

        return feedbackRepository.save(feedback);
    }

    /**
     * Call the ai api and classify the determined sentiment as negative positive or
     * neutral.
     * 
     * @param text
     * @return
     */
    private String analyzeSentiment(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String payload = String.format("{\"inputs\": \"%s\"}", text);
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(HUGGINGFACE_API_URL, entity, String.class);
            return extractSentimentLabel(response.getBody());
        } catch (Exception e) {
            // Log error and return neutral/fallback
            return "unknown";
        }
    }

    private String extractSentimentLabel(String json) {
        // quick-and-dirty parse — refine if needed
        if (json.contains("1 star"))
            return "negative";
        if (json.contains("2 stars"))
            return "negative";
        if (json.contains("3 stars"))
            return "neutral";
        if (json.contains("4 stars"))
            return "positive";
        if (json.contains("5 stars"))
            return "positive";
        return "unknown";
    }
}
