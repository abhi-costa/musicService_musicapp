# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Create app directory
VOLUME /tmp

# Copy the JAR file into the container
COPY target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
