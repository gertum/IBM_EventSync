# IBM Internship Exercise: Event Feedback Analyzer

A fictional platform called EventSync. It helps teams organize events 
like workshops, team buildings, or small conferences. After each event, participants 
leave written feedback. 
A backend application accepts this 
feedback and automatically analyzes it using an AI sentiment analysis API (
Hugging Face). The system classifies each feedback as 
positive, neutral, or negative and provides a summary per event.

## Project structure

Project backend code is located in src\main\java\com\ibm\event_sync folder.

* Web routes are defined in RouteController inside routing folder.
* API routes are defined in controller folder.

Views are located in src\main\resources\templates folder.

Tests are located in src\test\java\com\ibm\event_sync folder.


## Run

### Build

Run `./mvnw clean` for a clean rebuild.

### Run

Run `./mvnw spring-boot:run` to run app.

## Test

Run ./mvn test to run app tests.

