FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradle gradle
COPY gradlew gradlew.bat settings.gradle build.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV HTTP_HOST=0.0.0.0
ENV HTTP_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
