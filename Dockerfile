# Use a lightweight base image with Java
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar file to the container
COPY target/event-sync-0.0.1-SNAPSHOT.jar event-sync.jar

# Expose the port your app runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "event-sync.jar"]
