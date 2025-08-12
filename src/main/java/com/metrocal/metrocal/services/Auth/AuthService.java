package com.metrocal.metrocal.services.Auth;

import com.metrocal.metrocal.dto.SignupRequest;
import com.metrocal.metrocal.dto.UserDto;

public interface AuthService {
    UserDto createTechnicien(SignupRequest signupRequest);
    UserDto createClient(SignupRequest signupRequest);
}
