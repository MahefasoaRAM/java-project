package com.bibliotheque.apiservice.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bibliotheque.apiservice.dto.UserDTO;
import com.bibliotheque.apiservice.entity.User;
import com.bibliotheque.apiservice.enums.RoleUser;
import com.bibliotheque.apiservice.request.UserRequest;

@Component
public class UserMapper {
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserMapper(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public User toEntity(UserRequest userRequest, RoleUser roleUser) {
    User user = new User();
    user.setNom(userRequest.getNom());
    user.setEmail(userRequest.getEmail());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    user.setRole(roleUser);
    user.setDeleted(userRequest.isDeleted());
    return user;
  }

  public UserDTO toDTO(User user) {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId());
    userDTO.setNom(user.getNom());
    userDTO.setEmail(user.getEmail());
    userDTO.setPassword(user.getPassword());
    userDTO.setRole(user.getRole().name());
    userDTO.setDeleted(user.isDeleted());
    return userDTO;
  }
}
