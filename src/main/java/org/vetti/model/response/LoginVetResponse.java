package org.vetti.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LoginVetResponse {
    private String message;
    private int statusCode;
    private String role;
    private Long id;
    private String status;
    private String payment;

    public LoginVetResponse(String message, int statusCode){
        this.message = message;
        this.statusCode = statusCode;
    }
}

