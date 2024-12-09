package org.vetti.model.dto;

import lombok.*;
import org.vetti.model.request.VetRequest;

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
    private String district;
    private String payment;

    public UpdateVetDTO(VetRequest vetRequest) {
        this.email = vetRequest.getEmail();
        this.name = vetRequest.getName();
        this.status = vetRequest.getStatus();
    }

}
