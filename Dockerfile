# Estágio 1: Build
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle src /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build -x test

# Estágio 2: Execução
FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]