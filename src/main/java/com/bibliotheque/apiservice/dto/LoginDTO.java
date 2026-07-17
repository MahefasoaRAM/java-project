package com.bibliotheque.apiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
  private String token;
  private UserDTO user;
}
