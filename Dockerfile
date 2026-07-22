# Stage 1: Build the application
FROM maven:3.9.9-amazoncorretto-21 AS builder
WORKDIR /app

# Copy the Project files (pom.xml) and download dependencies
COPY . .

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk
COPY --from=builder /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]