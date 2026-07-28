package com.project.chitti.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanTransactionResponseDTO {
    private Long transactionId;
    private Long paidAmount;
    private String paymentMethod;
    private LocalDateTime paidOn;
}