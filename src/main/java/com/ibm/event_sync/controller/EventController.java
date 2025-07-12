package com.ibm.event_sync.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ibm.event_sync.dto.EventSummary;
import com.ibm.event_sync.entity.Event;
import com.ibm.event_sync.entity.Feedback;
import com.ibm.event_sync.repository.EventRepository;
import com.ibm.event_sync.repository.FeedbackRepository;
import com.ibm.event_sync.service.FeedbackService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private FeedbackRepository feedbackRepository;

  @GetMapping
  public Iterable<Event> findAll() {
    return eventRepository.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Event create(@RequestBody @Valid Event event) {
    return eventRepository.save(event);
  }

  @PostMapping("/{id}/feedback")
  @ResponseStatus(HttpStatus.CREATED)
  public Feedback addFeedback(@PathVariable Long id, @RequestBody @Valid Feedback feedback) {

    ///////////
    /// call service instead of the rep directly
    ///

    FeedbackService feedbackService = new FeedbackService(feedbackRepository, eventRepository);
    return feedbackService.addFeedback(id, feedback);

    // Event event = eventRepository.findById(id)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event
    // not found"));

    // feedback.setEvent(event);
    // return feedbackRepository.save(feedback);
  }

  @GetMapping("/{id}/feedback")
  public ResponseEntity<EventSummary> getEventFeedback(@PathVariable Long id) {
    Optional<Event> eventOptional = eventRepository.findById(id);
    if (!eventOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Event event = eventOptional.get();
    long count = feedbackRepository.countByEventId(id);
    List<Feedback> feedbackList = feedbackRepository.findByEventId(id);

    EventSummary summary = new EventSummary(
        event.getId(),
        event.getTitle(),
        event.getDescription(),
        count,
        feedbackList);

    return ResponseEntity.ok(summary);
  }
}
