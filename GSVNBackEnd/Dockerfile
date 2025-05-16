FROM ubuntu:latest
LABEL authors="ADMIN"

ENTRYPOINT ["top", "-b"]
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
# Load environment variables from .env file
COPY .env /app/.env
RUN set -o dotglob &&  export $(grep -v '^#' /app/.env | xargs -d '\n' | sed -e 's/= /=/g' | awk '{print "ENV " $0}') && set +o dotglob
ENTRYPOINT ["java", "-jar", "app.jar"]