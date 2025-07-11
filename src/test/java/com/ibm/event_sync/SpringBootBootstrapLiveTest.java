package com.ibm.event_sync;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.ibm.event_sync.entity.Event;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.apache.commons.text.RandomStringGenerator;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootBootstrapLiveTest {

    @LocalServerPort
    private int port;
    private String API_ROOT;

    @BeforeEach
    public void setUp() {
        API_ROOT = "http://localhost:" + port + "/api/events";
        RestAssured.port = port;
    }

    private Event createRandomEvent() {
        final Event event = new Event();
        event.setTitle(randomAlphabetic(10));
        event.setDescription(randomAlphabetic(60));
        return event;
    }

    private String randomAlphabetic(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('A', 'z')
                .filteredBy(Character::isLetter)
                .build();
        return generator.generate(length);
    }

    // private String createEventAsUri(Event event) {
    // final Response response = RestAssured.given()
    // .contentType(MediaType.APPLICATION_JSON_VALUE)
    // .body(event)
    // .post(API_ROOT);
    // return API_ROOT + "/" + response.jsonPath().get("id");
    // }

    @Test
    public void whenGetAllEvents_thenOK() {
        Response response = RestAssured.get(API_ROOT);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    public void whenCreateNewEvent_thenCreated() {
        Event event = createRandomEvent();
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(event)
                .post(API_ROOT);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    public void whenInvalidEvent_thenError() {
        Event event = createRandomEvent();
        event.setDescription(null);
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(event)
                .post(API_ROOT);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }
}
