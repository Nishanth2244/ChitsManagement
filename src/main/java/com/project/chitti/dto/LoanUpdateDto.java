package com.project.chitti.dto;

import lombok.Data;

@Data
public class LoanUpdateDto {
	
	private Long loanAmount;
	private Long totalMonths;
	private Long emiAmount;

}
