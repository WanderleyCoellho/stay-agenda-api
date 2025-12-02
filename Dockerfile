# Etapa 1: Construção (Build)
# Usamos a imagem oficial do Maven com Java 21 (Eclipse Temurin)
FROM maven:3.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Execução (Run)
# Usamos uma imagem leve do Java 21 apenas para rodar o App
FROM eclipse-temurin:21-jre
COPY --from=build /target/stay-agenda-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]