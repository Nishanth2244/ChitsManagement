package com.project.chitti.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ChitLiftRequestDTO {
//    private Long chitId;
//    private Long userId;
	private Long chitMemberId;
    private Integer monthNumber; 
    private Long liftedAmount;   
    private String paymentMethod;
    private LocalDate liftedDate;
}