package com.ibm.event_sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaRepositories("com.ibm.event_sync.repository") 
@EntityScan("com.ibm.event_sync.entity")
@SpringBootApplication
@EnableAsync
public class EventSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSyncApplication.class, args);
	}

}
