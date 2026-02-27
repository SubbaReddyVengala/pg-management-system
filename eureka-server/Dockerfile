# Stage 1: Build is already done by mvn package — we just run the JAR
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR built by Maven
COPY target/*.jar app.jar

# Expose the service port
EXPOSE 8761

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app.jar"]
