package com.ibm.event_sync.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.event_sync.entity.Feedback.Sentiment;
import com.ibm.event_sync.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SentimentAnalysisService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final FeedbackRepository feedbackRepository;

    @Value("${hf.api.key}")
    private String API_KEY;

    private final String HUGGINGFACE_API_URL =
            "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment";

    public SentimentAnalysisService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Async
    public CompletableFuture<Void> analyzeAndUpdateSentimentAsync(Long feedbackId, String text) {
        System.out.println("[ASYNC] Running in thread: " + Thread.currentThread().getName());
        Sentiment sentiment = analyzeSentiment(text);

        feedbackRepository.findById(feedbackId).ifPresent(fb -> {
            fb.setSentiment(sentiment);
            feedbackRepository.save(fb);
        });

        return CompletableFuture.completedFuture(null);
    }

    private Sentiment analyzeSentiment(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            String payload = String.format("{\"inputs\": \"%s\"}", text);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(HUGGINGFACE_API_URL, entity, String.class);
            return extractSentimentLabel(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return Sentiment.PENDING;
        }
    }

    /**
     * 
     * @param json The AI model API returns a json array which contains the sentiment label and the sentiment probability.
     * This array is sorted by probability in decreasing order.
     * @return
     */
    private Sentiment extractSentimentLabel(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            // get first by probability sentiment in api response
            JsonNode firstArray = root.get(0);
            if (firstArray != null && firstArray.isArray() && firstArray.size() > 0) {
                // get the label of the sentiment
                String label = firstArray.get(0).get("label").asText();
                return switch (label) {
                    case "1 star", "2 stars" -> Sentiment.NEGATIVE;
                    case "3 stars" -> Sentiment.NEUTRAL;
                    case "4 stars", "5 stars" -> Sentiment.POSITIVE;
                    default -> Sentiment.PENDING;
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Sentiment.PENDING;
    }
}
