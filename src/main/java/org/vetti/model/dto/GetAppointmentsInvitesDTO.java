package org.vetti.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetAppointmentsInvitesDTO {

    private String name;
    private String email;
    private String status;
}
