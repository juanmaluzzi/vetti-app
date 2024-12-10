package org.vetti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@EnableScheduling
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        if (System.getenv("ENVIRONMENT") == null || System.getenv("ENVIRONMENT").equals("development")) {
            try {
                loadEnvFile(".env");
                System.out.println("Variables cargadas desde .env en local.");
            } catch (IOException e) {
                System.out.println("Error cargando el archivo .env: " + e.getMessage());
            }
        } else {
            System.out.println("Se usaron las variables del yaml.");
        }

        SpringApplication.run(Main.class, args);
    }


    private static void loadEnvFile(String filePath) throws IOException {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        properties.setProperty(key, value);
                        System.setProperty(key, value);
                    }
                }
            }
        }
    }
}
