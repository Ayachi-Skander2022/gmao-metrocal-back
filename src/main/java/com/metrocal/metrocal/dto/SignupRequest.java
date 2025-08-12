package com.metrocal.metrocal.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String fullName;
    private String adresse;
    private String telephone;
}
