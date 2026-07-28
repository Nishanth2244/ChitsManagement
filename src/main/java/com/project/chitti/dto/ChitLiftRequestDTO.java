package com.project.chitti.dto;

import lombok.Data;

@Data
public class ChitLiftRequestDTO {
    private Long chitId;
    private Long userId;
    private Integer monthNumber; 
    private Long liftedAmount;   
    private String paymentMethod; 
}