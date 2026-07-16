package com.project.chitti.dto;

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
    private String phoneNo;
    private LocalDateTime joinedAt;
    private boolean status;
}