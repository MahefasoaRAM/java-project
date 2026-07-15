package com.bibliotheque.apiservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bibliotheque.apiservice.dto.UserDTO;
import com.bibliotheque.apiservice.enums.RoleUser;
import com.bibliotheque.apiservice.request.UserRequest;
import com.bibliotheque.apiservice.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @PostMapping(consumes = MediaType.ALL_VALUE)
  public ResponseEntity<UserDTO> createUser(@RequestBody UserRequest userRequest, RoleUser roleUser) {
    UserDTO createdUser = userService.createUser(userRequest, roleUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/role/{roleUser}")
  public ResponseEntity<List<UserDTO>> getAllUsersByRole(@PathVariable("roleUser") RoleUser roleUser) {
    List<UserDTO> users = userService.getAllUsersByRole(roleUser);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
    UserDTO user = userService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
    UserDTO user = userService.getUserByEmail(email);
    return ResponseEntity.ok(user);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id, @RequestBody UserRequest userRequest) {
    UserDTO updatedUser = userService.updateUser(id, userRequest);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
