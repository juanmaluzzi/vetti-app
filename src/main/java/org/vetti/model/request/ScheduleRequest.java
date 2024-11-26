package org.vetti.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Pattern;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {

    private List<DaySchedule> days;

    private String vetEmail;

    private String vetName;

    private String service;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DaySchedule {

        @Pattern(regexp = "^(?i)(lunes|martes|miércoles|jueves|viernes|sábado|domingo)$", message = "Día inválido")
        private String day;

        private List<TimeSlot> timeSlots;

    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeSlot {

        @Pattern(regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d$", message = "Invalid time format, must be HH:mm")
        private String from;

        @Pattern(regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d$", message = "Invalid time format, must be HH:mm")
        private String to;

    }
}
