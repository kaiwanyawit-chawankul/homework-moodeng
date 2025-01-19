# Use a multi-stage build to reduce image size
FROM gradle:7.5.1-jdk17 AS builder

WORKDIR /app

# Copy only the necessary files for the build
COPY gradle gradle
COPY gradlew gradlew.bat ./
COPY build.gradle.kts settings.gradle.kts ./
COPY src src

# Build the application
RUN chmod +x gradlew
RUN ./gradlew build

# Create a smaller image for running the application
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the application JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Copy the application config
COPY src/main/resources/application.conf /app/application.conf

# Copy dependencies from the build stage - THIS IS THE KEY CHANGE
COPY --from=builder /app/build/libs/ /app/libs/

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
#ENTRYPOINT ["sh"]