package com.project.chitti.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Installments {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "chit_member_id")
	private ChitMembers chitMember;
	
	private Integer monthNumber;
	private Long expectedAmt;
	private Long paidAmt;
	
	private String status; // "PENDING", "PARTIAL", "PAID"

	
}