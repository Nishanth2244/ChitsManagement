package com.project.chitti.dto;

import lombok.Data;

@Data
public class ChitUpdateRequestDTO {
    private String chitName;
    private Long totalAmount;
    private Long totalMonths;
    private Long installmentAmt;
}