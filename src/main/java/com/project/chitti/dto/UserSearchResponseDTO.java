package com.project.chitti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSearchResponseDTO {
    private Long id;
    private String name;
    private String phoneNo;
    private String address;
    private boolean status;
}