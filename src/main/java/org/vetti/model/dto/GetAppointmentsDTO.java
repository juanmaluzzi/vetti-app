package org.vetti.model.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetAppointmentsDTO {

    private String createdAt;
    private String updatedAt;
    private String startTime;
    private String endTime;
    private String vetEmail;
    private String vetName;
    private String eventName;
    private String status;
    private String location;
    private List<GetAppointmentsInvitesDTO> invitees;
}
