# Use OpenJDK 17 on Alpine
FROM openjdk:23

# Create an app directory and set it as the working directory
WORKDIR /app

# Copy your built JAR into the container
COPY target/follow-service.jar follow-service.jar

# Expose the port your service runs on (if you want clarity, not strictly required for K8s)
EXPOSE 8083

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/follow-service.jar"]
