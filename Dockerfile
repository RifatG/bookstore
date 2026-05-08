# Этап сборки (если нужна, но у тебя уже готовый jar)
# FROM maven:3.8-openjdk-17 AS build
# COPY . .
# RUN mvn clean package

# Финальный образ
FROM eclipse-temurin:21-jre

# Создаем непривилегированного пользователя
RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --gid 1001 appuser

WORKDIR /app

# Копируем JAR (лучше использовать конкретное имя или wildcard)
COPY target/*.jar app.jar
COPY src/main/resources/spring-frontend /app/spring-frontend

# Настройки JVM для контейнера
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+UseG1GC"

# Открываем порт
EXPOSE 8085

# Переключаемся на непривилегированного пользователя
USER appuser

# Запускаем с оптимизированными параметрами
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]