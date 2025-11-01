FROM eclipse-temurin:21-jre-alpine
LABEL authors="omori"

COPY build/libs/user-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]