package org.vetti.templates;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class PaymentTemplate {

    public String loadPaymentTemplate(String templateName, Map<String, String> variables) throws IOException {

        Path path = new ClassPathResource("templates/" + templateName).getFile().toPath();
        String content = Files.readString(path);

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return content;
    }
}
