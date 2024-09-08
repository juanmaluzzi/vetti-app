package org.vetti;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        // Cargar las variables de entorno desde el archivo .env usando Dotenv
        Dotenv dotenv = Dotenv.load();

        // Establecer propiedades del sistema para que Spring Boot pueda usarlas
        System.setProperty("AUTH0_CLIENT_ID", dotenv.get("AUTH0_CLIENT_ID"));
        System.setProperty("AUTH0_CLIENT_SECRET", dotenv.get("AUTH0_CLIENT_SECRET"));
        System.setProperty("AUTH0_ISSUER_URI", dotenv.get("AUTH0_ISSUER_URI"));
        System.setProperty("AUTH0_REDIRECT_URI", dotenv.get("AUTH0_REDIRECT_URI"));
        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));

        SpringApplication.run(Main.class, args);
    }
}
