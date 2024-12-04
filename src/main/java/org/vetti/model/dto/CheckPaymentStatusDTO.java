package org.vetti.model.dto;


import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CheckPaymentStatusDTO {

    private String paymentId;

    private Long vetId;
}
