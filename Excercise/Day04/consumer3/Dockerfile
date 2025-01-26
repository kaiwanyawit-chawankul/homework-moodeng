# Use a specific sbt base image with Eclipse Temurin JDK 17
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy build definition files first for better caching
COPY build.sbt .
COPY project/ project/

# Resolve dependencies and plugins (this will be cached)
RUN sbt update

# Copy the application source code
COPY src/ src/

# Compile and create the assembly (fat JAR)
RUN sbt compile assembly

# --- Final stage: Create a smaller image with only the JRE ---
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the assembled JAR from the builder stage
# COPY --from=builder /app/target/scala-*.jar /app/app.jar
COPY --from=builder /app/target/scala-2.13/app.jar /app/app.jar

# Expose the port your application listens on (replace with your port)
EXPOSE 8083

# Set the command to run the application
CMD ["java", "-jar", "/app/app.jar"]