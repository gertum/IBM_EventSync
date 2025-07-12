package com.ibm.event_sync.repository;

import com.ibm.event_sync.entity.Feedback;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FeedbackRepository extends CrudRepository<Feedback, Long> {
    long countByEventId(Long eventId);

    List<Feedback> findByEventId(Long eventId);
}
