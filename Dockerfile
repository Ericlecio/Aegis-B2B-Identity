# Estágio 1: Build (Compilação)
FROM gradle:8.5-jdk21 AS build
WORKDIR /home/gradle/project

# Copiamos todos os arquivos
COPY --chown=gradle:gradle . .

RUN chmod +x gradlew

RUN ./gradlew build -x test --no-daemon

# Estágio 2: Execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
EXPOSE 8080

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]