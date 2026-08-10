# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app


COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline


COPY src ./src
RUN ./mvnw package -DskipTests


FROM eclipse-temurin:21-jdk-alpine AS extract
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache fontconfig ttf-dejavu gcompat
USER root
RUN mkdir -p /usr/share/fonts/truetype/custom

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=extract /app/dependencies/ ./
COPY --from=extract /app/spring-boot-loader/ ./
COPY --from=extract /app/snapshot-dependencies/ ./
COPY --from=extract /app/application/ ./
RUN if [ -f /app/BOOT-INF/classes/fonts/times.ttf ]; then \
        cp /app/BOOT-INF/classes/fonts/times.ttf /usr/share/fonts/truetype/custom/ && \
        fc-cache -f -v; \
    fi
RUN chown -R spring:spring /app
USER spring:spring
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]
