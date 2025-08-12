package com.metrocal.metrocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTechDto {
    
    private Long id;
    private String fullName;
    private String adresse;
    private String telephone;
}
