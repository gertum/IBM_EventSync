package com.ibm.event_sync.service;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ibm.event_sync.entity.Event;
import com.ibm.event_sync.entity.Feedback;
import com.ibm.event_sync.entity.Feedback.Sentiment;
import com.ibm.event_sync.repository.EventRepository;
import com.ibm.event_sync.repository.FeedbackRepository;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final SentimentAnalysisService sentimentService;

    public FeedbackService(FeedbackRepository feedbackRepository, EventRepository eventRepository, SentimentAnalysisService sentimentService) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
        this.sentimentService = sentimentService;
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
        feedback.setSentiment(Sentiment.PENDING);
        Feedback savedFeedback = feedbackRepository.save(feedback);

        System.out.println("Synchrounous part which has pending sentiment: "+ savedFeedback.getSentiment()); /// TODO remove

        // Kick off async sentiment analysis
        sentimentService.analyzeAndUpdateSentimentAsync(savedFeedback.getId(), feedback.getText());

        
        System.out.println("The part where sentiment could be anything ;) : " + savedFeedback.getSentiment()); /// TODO remove

        return savedFeedback;
    }
}
