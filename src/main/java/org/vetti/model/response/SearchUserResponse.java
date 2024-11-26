package org.vetti.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vetti.model.dto.PetDTO;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserResponse {

    private Long id;
    private int statusCode;
    private String message;
    private String email;
    private String name;
    private String lastName;
    private String phoneNumber;
    private String role;
    private String dni;
    private String address;
    private String district;
    private List<PetDTO> pets;

    public SearchUserResponse(String message, int statusCode){
        this.message = message;
        this.statusCode = statusCode;
    }
}
