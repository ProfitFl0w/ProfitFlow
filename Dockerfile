## ===========
## Build stage
## ===========
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Кэшируем зависимости Maven отдельно от исходников
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -B -DskipTests package

## ===========
## Runtime stage
## ===========
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Небольшие утилиты и таймзона (если нужно корректное локальное время)
RUN apk add --no-cache tzdata && \
    addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/core-app-0.0.1-SNAPSHOT.jar app.jar

# Порт приложения Spring Boot (Fly.io будет проксировать его наружу)
EXPOSE 8080

# Базовые JVM-настройки для контейнера
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

# Профиль продакшена — БД (Neon), кеши (Upstash) и т.п. настраиваются через ENV
# например:
#   SPRING_DATASOURCE_URL=jdbc:postgresql://<neon-host>:<port>/<db>
#   SPRING_DATASOURCE_USERNAME=...
#   SPRING_DATASOURCE_PASSWORD=...
#   SPRING_REDIS_URL=rediss://:<password>@<upstash-host>:<port>
ENV SPRING_PROFILES_ACTIVE=prod

USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]