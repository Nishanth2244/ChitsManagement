package com.project.chitti.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
	
//	private Long installmentId;
//    private Long userId;
//    private Long chitId;
	private Long chitMemberId;
    private Integer monthNumber; 
    private Long amount;
    private String paymentMethod; // "CASH" or "UPI"
    private Long fineAmount;
}