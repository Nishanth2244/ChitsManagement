package com.project.chitti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstallmentDetailsDTO {
    private Long installmentId; // To fetch transaction details
    private Integer monthNumber;
    private Long expectedAmt;
    private Long paidAmt;
    private String status; // "PENDING", "PARTIAL", "PAID"
}