package com.metrocal.metrocal.services.Jwt;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.metrocal.metrocal.dto.UserDto;




public interface UserService{
 
    UserDetailsService userDetailsService();

   List<UserDto> getAllTechniciens() ;

   
   List<UserDto> getAllClients() ;



}
