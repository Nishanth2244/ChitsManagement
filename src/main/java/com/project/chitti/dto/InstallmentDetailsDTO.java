package com.project.chitti.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstallmentDetailsDTO {
    private Long installmentId; // To fetch transaction details
    private Integer monthNumber;
    private Long expectedAmt;
    private Long paidAmt;
    private String status; // "PENDING", "PARTIAL", "PAID"
    
//    private boolean isLifted; 
    private Long liftedAmount;
    private LocalDateTime liftedDate;
}