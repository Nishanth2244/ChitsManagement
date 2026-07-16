package com.project.chitti.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Chits {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private Long totalAmount;
	private Long totalMonths;
	private Long installmentAmt;
	private boolean status;
	private LocalDateTime createdAt;
	
	
//	@OneToMany(mappedBy = "chit")
//	private List<ChitMembers> chitMembers;
}
