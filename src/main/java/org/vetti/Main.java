package org.vetti;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = null;
        // Cargar las variables de entorno desde el archivo .env usando Dotenv
        if (System.getenv("ENVIRONMENT") == null || System.getenv("ENVIRONMENT").equals("development")) {
            dotenv = Dotenv.load();
        }

        // Establecer propiedades del sistema, ya sea desde dotenv o directamente desde las variables de entorno
        System.setProperty("AUTH0_CLIENT_ID",
                (dotenv != null) ? dotenv.get("AUTH0_CLIENT_ID") : System.getenv("AUTH0_CLIENT_ID"));
        System.setProperty("AUTH0_CLIENT_SECRET",
                (dotenv != null) ? dotenv.get("AUTH0_CLIENT_SECRET") : System.getenv("AUTH0_CLIENT_SECRET"));
        System.setProperty("AUTH0_ISSUER_URI",
                (dotenv != null) ? dotenv.get("AUTH0_ISSUER_URI") : System.getenv("AUTH0_ISSUER_URI"));
        System.setProperty("AUTH0_REDIRECT_URI",
                (dotenv != null) ? dotenv.get("AUTH0_REDIRECT_URI") : System.getenv("AUTH0_REDIRECT_URI"));
        System.setProperty("SPRING_DATASOURCE_URL",
                (dotenv != null) ? dotenv.get("SPRING_DATASOURCE_URL") : System.getenv("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME",
                (dotenv != null) ? dotenv.get("SPRING_DATASOURCE_USERNAME") : System.getenv("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD",
                (dotenv != null) ? dotenv.get("SPRING_DATASOURCE_PASSWORD") : System.getenv("SPRING_DATASOURCE_PASSWORD"));
        SpringApplication.run(Main.class, args);
    }
}
