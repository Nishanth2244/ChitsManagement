package com.project.chitti.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanInstallmentResponseDTO {
    private Long installmentId;
    private Integer monthNumber;
    private Long expectedAmt; 
    private Long paidAmt;     
    private Long dueAmt;      
    private String status;    
    private LocalDateTime dueDate;
}