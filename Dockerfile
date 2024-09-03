# Usa una imagen base de Java
FROM openjdk:17-jdk-alpine

# Configura el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR generado por tu build
COPY target/vetti-app-1.0.0.jar app.jar

# Expone el puerto en el contenedor
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
