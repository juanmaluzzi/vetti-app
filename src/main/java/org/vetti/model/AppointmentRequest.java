package org.vetti.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppointmentRequest {
    private String vetId;
    private String eventType;
    private String inviteeName;
    private String inviteeEmail;
}
