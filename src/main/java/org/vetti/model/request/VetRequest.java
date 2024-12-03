package org.vetti.model.request;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vets")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class VetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cuit;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "varchar(255) default '0'")
    private String role = "2";

    @Column
    private String district;

    @Column
    private String status = "disabled";

    @Column(name = "is_emergency_vet", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isEmergencyVet = false;

    @Column(name = "calendly_email", columnDefinition = "varchar(255)")
    private String calendlyEmail;

    @Column(name = "calendly_calendar", columnDefinition = "varchar(255)")
    private String calendlyCalendar;

    @Column
    private String payment = "pending";

    @Column(name = "password_reset_code", length = 6)
    private String passwordResetCode;

    @Column(name = "password_reset_expiry")
    private LocalDateTime passwordResetExpiry;

}
