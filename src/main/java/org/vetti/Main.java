package org.vetti;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = null;

        // Solo cargar el archivo .env si estamos en un entorno local (desarrollo)
        if (System.getenv("ENVIRONMENT") == null || System.getenv("ENVIRONMENT").equals("development")) {
            dotenv = Dotenv.load();  // Cargar el .env en local
            System.out.println("Cargando variables desde .env en entorno de desarrollo");
        } else {
            System.out.println("Usando variables de entorno configuradas en producción");
        }

        // Establecer propiedades del sistema ya sea desde dotenv (local) o desde System.getenv (prod)
        setSystemProperty("AUTH0_CLIENT_ID", dotenv);
        setSystemProperty("AUTH0_CLIENT_SECRET", dotenv);
        setSystemProperty("AUTH0_ISSUER_URI", dotenv);
        setSystemProperty("AUTH0_REDIRECT_URI", dotenv);
        setSystemProperty("SPRING_DATASOURCE_URL", dotenv);
        setSystemProperty("SPRING_DATASOURCE_USERNAME", dotenv);
        setSystemProperty("SPRING_DATASOURCE_PASSWORD", dotenv);

        SpringApplication.run(Main.class, args);
    }

    private static void setSystemProperty(String key, Dotenv dotenv) {
        String value = (dotenv != null) ? dotenv.get(key) : System.getenv(key);
        if (value != null) {
            System.setProperty(key, value);
        } else {
            System.out.println("Warning: Variable de entorno " + key + " no está definida.");
        }
    }
}
