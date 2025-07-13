package com.ibm.event_sync.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

@Component
public class SentimentQueue {
    private final BlockingQueue<FeedbackRequest> queue = new LinkedBlockingQueue<>();

    public void enqueue(FeedbackRequest request) {
        queue.offer(request);
    }

    public FeedbackRequest dequeue() throws InterruptedException {
        return queue.take(); // blocks if empty
    }
}

