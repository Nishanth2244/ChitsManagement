package com.project.chitti.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanSummaryResponseDTO {
    private Long loanId;
    private Long loanAmount;
    private Long totalMonths;
    private Long emiAmount;
    private LocalDateTime issuedDate;
    private String status; // ACTIVE or CLOSED
}