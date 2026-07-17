package com.bibliotheque.apiservice.service;

import java.util.List;

import com.bibliotheque.apiservice.dto.UserDTO;
import com.bibliotheque.apiservice.enums.RoleUser;
import com.bibliotheque.apiservice.request.UserRequest;

public interface UserService {
  UserDTO createUser(UserRequest userRequest, RoleUser roleUser);

  List<UserDTO> getAllUsers();

  List<UserDTO> getAllUsersByRole(RoleUser roleUser);

  UserDTO getUserById(Long id);

  UserDTO getUserByEmail(String email);

  UserDTO updateUser(Long id, UserRequest userRequest);

  void deleteUser(Long id);
}
