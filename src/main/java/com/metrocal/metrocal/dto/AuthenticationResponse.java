package com.metrocal.metrocal.dto;

import com.metrocal.metrocal.entities.UserRole;

import lombok.Data;

@Data
public class AuthenticationResponse {
     private String jwt;

    private Long userId;

    private UserRole userRole;
}
