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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.event_sync.entity.Event;
import com.ibm.event_sync.entity.Feedback;
import com.ibm.event_sync.repository.EventRepository;
import com.ibm.event_sync.repository.FeedbackRepository;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

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
        feedback.setSentiment("pending");
        Feedback savedFeedback = feedbackRepository.save(feedback);

        System.out.println("Synchrounous part which has pending sentiment: "+ savedFeedback.getSentiment()); /// TODO remove

        // Kick off async sentiment analysis
        analyzeAndUpdateSentimentAsync(savedFeedback.getId(), feedback.getText());

        
        System.out.println("The part where sentiment could be anything ;) : " + savedFeedback.getSentiment()); /// TODO remove

        return savedFeedback;
    }

    @Async
    public CompletableFuture<Void> analyzeAndUpdateSentimentAsync(Long feedbackId, String text) {
        System.out.println("Starting async sentiment analysis..."); /// TODO remove

        String sentiment = analyzeSentiment(text);
        System.out.println("Sentiment result: " + sentiment); /// TODO remove

        feedbackRepository.findById(feedbackId).ifPresent(fb -> {
            fb.setSentiment(sentiment);
            feedbackRepository.save(fb);
            System.out.println("Feedback updated with sentiment: " + sentiment); /// TODO remove
        });

        return CompletableFuture.completedFuture(null);
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            // root is an array, get first element which is another array
            JsonNode firstArray = root.get(0);
            if (firstArray != null && firstArray.isArray() && firstArray.size() > 0) {
                JsonNode firstElement = firstArray.get(0);
                String label = firstElement.get("label").asText();

                switch (label) {
                    case "1 star":
                    case "2 stars":
                        return "negative";
                    case "3 stars":
                        return "neutral";
                    case "4 stars":
                    case "5 stars":
                        return "positive";
                    default:
                        return "unknown";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }
}
