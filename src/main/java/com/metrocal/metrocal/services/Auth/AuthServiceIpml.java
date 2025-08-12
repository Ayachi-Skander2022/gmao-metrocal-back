package com.metrocal.metrocal.services.Auth;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.SignupRequest;
import com.metrocal.metrocal.dto.UserDto;
import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.entities.UserRole;
import com.metrocal.metrocal.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AuthServiceIpml implements AuthService{

   private final UserRepository userRepository;

    @PostConstruct
    public void createAdminAccount(){
        Optional<User> adminAccount = userRepository.findByUserRole(UserRole.ADMIN);
        if (adminAccount.isEmpty()) {
            User user = new User();
            user.setEmail("admin@metrocal.com");
            user.setFullName("Admin");
            user.setUserRole(UserRole.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepository.save(user);
            System.out.println("Admin account created successfully");

        }else{
            System.out.println("Admin account alredy exist");
        }
    }

    public UserDto createTechnicien(SignupRequest signupRequest){
        if (userRepository.findFirstByEmail(signupRequest.getEmail()).isPresent()) {
            throw new EntityExistsException("TECHNICIEN Already Present With email" + signupRequest.getEmail());
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFullName(signupRequest.getFullName());
        user.setUserRole(UserRole.TECHNICIEN);
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setAdresse(signupRequest.getEmail());
        user.setTelephone(signupRequest.getTelephone());
        user.setAdresse(signupRequest.getAdresse());
        User createdUser = userRepository.save(user);
        return createdUser.getUserDto();

    }

    @Override
    public UserDto createClient(SignupRequest signupRequest) {
       if (userRepository.findFirstByEmail(signupRequest.getEmail()).isPresent()) {
            throw new EntityExistsException("Technicien Already Present With email" + signupRequest.getEmail());
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFullName(signupRequest.getFullName());
        user.setUserRole(UserRole.CLIENT);
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setTelephone(signupRequest.getTelephone());
        user.setAdresse(signupRequest.getAdresse());
        User createdUser = userRepository.save(user);
        return createdUser.getUserDto();
    }
}
