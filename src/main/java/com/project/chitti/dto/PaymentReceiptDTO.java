package com.project.chitti.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentReceiptDTO {
    private Long transactionId;      
    private String chitName;
    private String memberName;
    private String phoneNo;          
    private Integer monthNumber;
    private Long paidAmount;        
    private String paymentMethod;
    private LocalDateTime paidOn;
    
    private Long monthExpectedAmount; 
    private Long monthBalanceDue;     
    private String installmentStatus;
}