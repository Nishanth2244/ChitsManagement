package com.project.chitti.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
	
	
	private Long id;
	private String name;
	private String phoneNo;
	private String address;
	private boolean status;
	private LocalDateTime createdAt;

}
