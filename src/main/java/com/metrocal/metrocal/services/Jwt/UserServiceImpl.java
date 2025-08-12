package com.metrocal.metrocal.services.Jwt;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.UserDto;
import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.entities.UserRole;
import com.metrocal.metrocal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

     private final UserRepository userRepository;

  
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
              return userRepository.findFirstByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
            }


        };
    }

    

public List<UserDto> getAllTechniciens() {
    return userRepository.findTechByUserRole(UserRole.TECHNICIEN)
                         .stream()
                         .map(User::getUserDto)
                         .collect(Collectors.toList());
}



public List<UserDto> getAllClients() {
    return userRepository.findTechByUserRole(UserRole.CLIENT)
                         .stream()
                         .map(User::getUserDto)
                         .collect(Collectors.toList());
}




}
