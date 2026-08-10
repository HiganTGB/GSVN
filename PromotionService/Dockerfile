# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle/ gradle/


RUN ./gradlew build -x test --no-daemon > /dev/null 2>&1 || true


COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon


FROM eclipse-temurin:21-jdk-alpine AS extract
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=extract /app/dependencies/ ./
COPY --from=extract /app/spring-boot-loader/ ./
COPY --from=extract /app/snapshot-dependencies/ ./
COPY --from=extract /app/application/ ./

RUN chown -R spring:spring /app
USER spring:spring

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]