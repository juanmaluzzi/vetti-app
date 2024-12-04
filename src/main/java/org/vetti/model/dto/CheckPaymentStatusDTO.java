package org.vetti.model.dto;


import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CheckPaymentStatusDTO {

    private String preApprovalId;

    private Long vetId;
}
