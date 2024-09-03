package org.vetti.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());  // Aquí se coloca el mensaje personalizado
        // Opcional: body.put("path", request.getRequestURI()); // Puedes incluir la ruta si necesitas

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Puedes agregar más manejadores para otras excepciones si es necesario
}
