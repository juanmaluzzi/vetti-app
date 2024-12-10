package org.vetti.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CanceledEventCalendlyDTO { //DTO para enviar un email a la veterinaria de cuando se canceló el evento(servicio)
    private String vetEmail;
    private String vetName;
    private String eventName;
}
