package com.ibm.event_sync.repository;

import com.ibm.event_sync.entity.Feedback;

import org.springframework.data.repository.CrudRepository;

public interface FeedbackRepository extends CrudRepository<Feedback, Long> {
}
