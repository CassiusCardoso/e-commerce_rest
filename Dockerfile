# Etapa de build
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests -DfinalName=app

# Etapa de execução
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/gerenciamento-pedidos-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
