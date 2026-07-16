package com.project.chitti.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChitResponseDTO {
	
	
	private Long id;
	private String name;
	private Long totalAmount;
	private Long totalMonths;
	private Long installmentAmt;
	private boolean status;
	private LocalDateTime createdAt;

}
