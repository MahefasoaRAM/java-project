package com.bibliotheque.apiservice.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bibliotheque.apiservice.dto.UserDTO;
import com.bibliotheque.apiservice.entity.User;
import com.bibliotheque.apiservice.enums.RoleUser;
import com.bibliotheque.apiservice.exception.DataConflictException;
import com.bibliotheque.apiservice.exception.ResourceNotFoundException;
import com.bibliotheque.apiservice.mapper.UserMapper;
import com.bibliotheque.apiservice.repository.UserRepository;
import com.bibliotheque.apiservice.request.UserRequest;
import com.bibliotheque.apiservice.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDTO createUser(UserRequest userRequest, RoleUser roleUser) {
    if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
      throw new DataConflictException("Email already exists: " + userRequest.getEmail());
    }
    User user = userMapper.toEntity(userRequest, roleUser);
    User savedUser = userRepository.save(user);
    return userMapper.toDTO(savedUser);
  }

  @Override
  public List<UserDTO> getAllUsers() {
    List<User> users = userRepository.findByDeletedFalse();
    return users.stream().map(userMapper::toDTO).toList();
  }

  @Override
  public List<UserDTO> getAllUsersByRole(RoleUser roleUser) {
    List<User> users = userRepository.findByRoleAndDeletedFalse(roleUser.name());
    return users.stream().map(userMapper::toDTO).toList();
  }

  @Override
  public UserDTO getUserById(Long id) {
    User user = userRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    return userMapper.toDTO(user);
  }

  @Override
  public UserDTO getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return userMapper.toDTO(user);
  }

  @Override
  public UserDTO updateUser(Long id, UserRequest userRequest) {
    User existingUser = userRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    if (userRequest.getNom() != null && !userRequest.getNom().isEmpty()
        && !userRequest.getNom().equals(existingUser.getNom())) {
      existingUser.setNom(userRequest.getNom());
    }
    if (userRequest.getEmail() != null && !userRequest.getEmail().isEmpty()
        && !userRequest.getEmail().equals(existingUser.getEmail())) {
      existingUser.setEmail(userRequest.getEmail());
    }
    if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()
        && !passwordEncoder.matches(userRequest.getPassword(), existingUser.getPassword())) {
      existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    }
    User savedUser = userRepository.save(existingUser);
    return userMapper.toDTO(savedUser);
  }

  @Override
  public void deleteUser(Long id) {
    User existingUser = userRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    existingUser.setDeleted(true);
    userRepository.save(existingUser);
  }
}