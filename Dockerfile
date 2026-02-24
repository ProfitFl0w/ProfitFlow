## ===========
## Build stage (Сборка внутри Docker на Java 21)
## ===========
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем pom.xml и качаем зависимости
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# Копируем исходники и собираем
COPY src ./src
RUN mvn -B -DskipTests package

## ===========
## Runtime stage (Минимальный образ для запуска)
## ===========
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Настройка таймзоны и пользователя (security first)
RUN apk add --no-cache tzdata && \
    addgroup -S app && adduser -S app -G app

# ВАЖНО: берем артефакт из этапа build
# Проверь, что имя jar файла именно такое, или используй *.jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"
ENV SPRING_PROFILES_ACTIVE=prod

USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]