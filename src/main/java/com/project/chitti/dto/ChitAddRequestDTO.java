package com.project.chitti.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ChitAddRequestDTO {

	private String chitName;
	private Long totalAmount;
	private Long totalMonths;
	private Long installmentAmt;
	private LocalDate startDate;
}
