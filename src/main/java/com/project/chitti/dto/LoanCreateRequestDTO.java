package com.project.chitti.dto;

import lombok.Data;

@Data
public class LoanCreateRequestDTO {
    private Long userId;
    private Long loanAmount;
    private Long totalMonths;
    private Long emiAmount; 

}