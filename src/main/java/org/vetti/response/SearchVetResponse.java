package org.vetti.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchVetResponse {

    private Long id;
    private int statusCode;
    private String message;
    private String email;
    private String name;
    private String address;
    private String phoneNumber;
    private String cuit;
    private String role;
    private Boolean isEmergencyVet;

    public SearchVetResponse(String message, int statusCode){
        this.message = message;
        this.statusCode = statusCode;
    }
}

