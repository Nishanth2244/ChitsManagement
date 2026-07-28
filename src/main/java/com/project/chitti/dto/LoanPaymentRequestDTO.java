package com.project.chitti.dto;

import lombok.Data;

@Data
public class LoanPaymentRequestDTO {
    private Long loanInstallmentId; 
    private Long amount;            
    private String paymentMethod;   
}