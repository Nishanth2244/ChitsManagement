package com.project.chitti.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChitMemberDetailsDTO {
    private Long chitMemberId;
    private Long userId;
    private String name;
    private String careOf;
    private String phoneNo;
    private LocalDate joinedAt;
    private boolean status;
}