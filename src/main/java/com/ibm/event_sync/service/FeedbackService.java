package com.ibm.event_sync.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ibm.event_sync.entity.Event;
import com.ibm.event_sync.entity.Feedback;
import com.ibm.event_sync.entity.Feedback.Sentiment;
import com.ibm.event_sync.queue.FeedbackRequest;
import com.ibm.event_sync.queue.SentimentQueue;
import com.ibm.event_sync.repository.EventRepository;
import com.ibm.event_sync.repository.FeedbackRepository;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final SentimentAnalysisService sentimentService;

    private final SentimentQueue sentimentQueue;

    public FeedbackService(FeedbackRepository feedbackRepository, EventRepository eventRepository,
            SentimentAnalysisService sentimentService, SentimentQueue sentimentQueue) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
        this.sentimentService = sentimentService;

        this.sentimentQueue = sentimentQueue;
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

        // Kick off async sentiment analysis
        sentimentService.analyzeAndUpdateSentimentAsync(savedFeedback.getId(), feedback.getText());

        return savedFeedback;
    }

    public void handleNewFeedback(Long feedbackId, String text) {
        sentimentQueue.enqueue(new FeedbackRequest(feedbackId, text));
    }
}
