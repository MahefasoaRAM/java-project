package com.bibliotheque.apiservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.bibliotheque.apiservice.dto.LoginDTO;
import com.bibliotheque.apiservice.dto.UserDTO;
import com.bibliotheque.apiservice.exception.BadRequestException;
import com.bibliotheque.apiservice.repository.UserRepository;
import com.bibliotheque.apiservice.request.LoginRequest;
import com.bibliotheque.apiservice.request.UserRequest;
import com.bibliotheque.apiservice.service.CustomerUserDetailsService;
import com.bibliotheque.apiservice.service.JwtService;
import com.bibliotheque.apiservice.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final UserRepository userRepository;
  private final CustomerUserDetailsService customerUserDetailsService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserRequest request) {
    if (request.getRole() == null) {
      throw new BadRequestException("Role is required");
    }

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
    }

    if (request.getPassword() == null || request.getPassword().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required");
    }

    if (request.getNom() == null || request.getNom().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name is required");
    }

    if (request.getEmail() == null || request.getEmail().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
    }

    UserDTO createdUser = userService.createUser(request, request.getRole());
    UserDetails userDetails = customerUserDetailsService.loadUserByUsername(request.getEmail());
    String token = jwtService.createToken(userDetails, createdUser.getRole(), createdUser.getId());
    return ResponseEntity.ok(new LoginDTO(token, createdUser));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    UserDTO user = userService.getUserByEmail(request.getEmail());
    if (user.getDeleted()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account is deleted");
    }

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = jwtService.createToken(userDetails, user.getRole(), user.getId());
      return ResponseEntity.ok(new LoginDTO(token, user));
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }
}
