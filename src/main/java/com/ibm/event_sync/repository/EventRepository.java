package com.ibm.event_sync.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.ibm.event_sync.entity.Event;

public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByTitle(String title);
}