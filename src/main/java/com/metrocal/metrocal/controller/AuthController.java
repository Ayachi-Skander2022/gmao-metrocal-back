package com.metrocal.metrocal.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metrocal.metrocal.dto.AuthenticationRequest;
import com.metrocal.metrocal.dto.AuthenticationResponse;
import com.metrocal.metrocal.dto.SignupRequest;
import com.metrocal.metrocal.dto.UserDto;
import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.repository.UserRepository;
import com.metrocal.metrocal.services.Auth.AuthService;
import com.metrocal.metrocal.services.Jwt.UserService;
import com.metrocal.metrocal.util.JwtUtil;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    
     private final AuthService authService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final UserService userService;
    
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signupClient")
    public ResponseEntity<?> signupClient(@RequestBody SignupRequest signupRequest){
        try {
            UserDto createUser = authService.createClient(signupRequest);
            return new ResponseEntity<>(createUser, HttpStatus.OK);
        }
        catch (EntityExistsException entityExistsException){
            return new ResponseEntity<>("Client Already exists", HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e){
            return new ResponseEntity<>("Client not created, come again later", HttpStatus.BAD_REQUEST);
        }
    }

     @PostMapping("/signupTech")
    public ResponseEntity<?> signupCompany(@RequestBody SignupRequest signupRequest){
        try {
            UserDto createUser = authService.createTechnicien(signupRequest);
            return new ResponseEntity<>(createUser, HttpStatus.OK);
        }
        catch (EntityExistsException entityExistsException){
            return new ResponseEntity<>("Technicien Already exists", HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e){
            return new ResponseEntity<>("Technicien not created, come again later", HttpStatus.BAD_REQUEST);
        }
    }
    

    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
          try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
          } catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect username o password.");
          }

          final UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getEmail());
          
          Optional<User> optionalUser = userRepository.findFirstByEmail(userDetails.getUsername());
          final String jwt = jwtUtil.generateToken((User) userDetails);


          AuthenticationResponse authenticationResponse = new AuthenticationResponse();

          if (optionalUser.isPresent()) {
            authenticationResponse.setJwt(jwt);
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
            authenticationResponse.setUserId(optionalUser.get().getId());
          }

          return authenticationResponse;
    }
}
