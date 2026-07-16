package com.project.chitti.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetailsDTO {
    private Long transactionId;
    private Long paidAmount;
    private String paymentMethod; // "CASH" or "UPI"
    private LocalDateTime paidOn;
}