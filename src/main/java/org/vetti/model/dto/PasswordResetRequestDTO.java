package org.vetti.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestDTO {

    @NotNull(message = "El email no puede ser nulo.")
    @Email(message = "Debe ser un email válido.")
    private String email;

}
