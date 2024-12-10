package org.vetti.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdateUserDTO {
    //agregar un address
    //country
    private String name;
    private String lastName;
    private String email; //quitar
    private String phoneNumber;
    private String role; //quitar
    private String password;
    private String dni;
    private String address;
    private String district;
}
