package org.vetti.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdateVetDTO {
    private String name;
    private String cuit;
    private String email;
    private String address;
    private String phoneNumber;
    private String role;
    private String status;
    private String password;
    private Boolean isEmergencyVet;
    private String calendlyEmail;
    private String calendlyCalendar;
}
