# Используем официальный образ Java
FROM openjdk:26-ea-jdk-slim

# Рабочая директория в контейнере
WORKDIR /app

# Копируем JAR приложения
COPY target/MyBookShopApp-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт
EXPOSE 8085

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]