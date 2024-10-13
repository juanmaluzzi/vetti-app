package org.vetti.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PetDTO {

    private Long id;
    private String name;
    private String type;

}
