package org.vetti.model.response;

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
    private String district;
    private String phoneNumber;
    private String cuit;
    private String role;
    private String status;
    private Boolean isEmergencyVet;
    private String calendlyEmail;
    private String calendlyCalendar;


    public SearchVetResponse(String message, int statusCode){
        this.message = message;
        this.statusCode = statusCode;
    }
}

