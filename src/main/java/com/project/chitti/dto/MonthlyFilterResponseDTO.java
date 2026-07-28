package com.project.chitti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyFilterResponseDTO {
    // Chit Info
    private Long chitId;
    private String chitName;
    
    // User Info
    private Long userId;
    private String memberName;
    private String phoneNo;
    
    // Month Info
    private Integer monthNumber;
    private Long expectedAmt;
    private Long paidAmt;
    private Long dueAmt; 
    private String status; 
}