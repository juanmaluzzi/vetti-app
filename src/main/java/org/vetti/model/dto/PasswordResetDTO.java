package org.vetti.model.dto;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDTO {

    @NotNull(message = "El email no puede ser nulo.")
    @Email(message = "Debe ser un email válido.")
    private String email;

    @NotBlank(message = "El código de recuperación no puede estar vacío.")
    private String code;

    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    private String newPassword;
}
