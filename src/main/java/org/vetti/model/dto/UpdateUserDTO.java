package org.vetti.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdateUserDTO {
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String password;
}
