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
public class ChitMembers {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "chit_id")
	private Chits chit;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users user;
	
	private LocalDateTime joinedAt;
	
	private boolean status;

}
