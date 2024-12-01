package org.vetti.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CancelCalendlyScheduleDTO {

    private String eventId;
    private String reason;
}
