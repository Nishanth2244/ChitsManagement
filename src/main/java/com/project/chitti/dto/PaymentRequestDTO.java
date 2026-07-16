package com.project.chitti.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long userId;
    private Long chitId;
    private Integer monthNumber; // Kotha field
    private Long amount;
    private String paymentMethod; // "CASH" or "UPI"
}