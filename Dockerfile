# Usa una imagen base de Maven para construir la aplicación
FROM maven:3.8.5-openjdk-17-slim AS build

# Configura el directorio de trabajo
WORKDIR /app

# Copia el archivo pom.xml y resuelve las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el código fuente y compila el proyecto
COPY src ./src
RUN mvn clean package

# Usa una imagen base de Java para correr la aplicación
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/vetti-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
