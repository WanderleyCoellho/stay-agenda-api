# Etapa 1: Construção (Build)
FROM maven:3.8.5-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Execução (Run)
FROM openjdk:21.0.1-jdk-slim
COPY --from=build /target/stay-agenda-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]