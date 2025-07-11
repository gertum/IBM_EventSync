package com.ibm.event_sync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;


import com.ibm.event_sync.entity.Event;
import com.ibm.event_sync.repository.EventRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public Iterable<Event> findAll() {
        return eventRepository.findAll();
    }

    // @GetMapping("/title/{bookTitle}")
    // public List findByTitle(@PathVariable String bookTitle) {
    //     return bookRepository.findByTitle(bookTitle);
    // }

    // @GetMapping("/{id}")
    // public Book findOne(@PathVariable Long id) {
    //     return bookRepository.findById(id)
    //       .orElseThrow(BookNotFoundException::new);
    // }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@RequestBody @Valid Event event) {
        return eventRepository.save(event);
    }

    // @DeleteMapping("/{id}")
    // public void delete(@PathVariable Long id) {
    //     bookRepository.findById(id)
    //       .orElseThrow(BookNotFoundException::new);
    //     bookRepository.deleteById(id);
    // }

    // @PutMapping("/{id}")
    // public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
    //     if (book.getId() != id) {
    //       throw new BookIdMismatchException();
    //     }
    //     bookRepository.findById(id)
    //       .orElseThrow(BookNotFoundException::new);
    //     return bookRepository.save(book);
    // }
}
