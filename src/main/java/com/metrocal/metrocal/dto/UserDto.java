package com.metrocal.metrocal.dto;

import com.metrocal.metrocal.entities.UserRole;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {


    public UserDto(Long id, String fullName, String adresse, String telephone) {
        this.id = id;
        this.fullName = fullName;
        this.adresse = adresse;
        this.telephone = telephone;

    }
    private Long id;
    private String email;
    private String fullName;
    private String adresse;
    private String telephone;
    private UserRole userRole;


}
