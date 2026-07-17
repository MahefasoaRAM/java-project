package com.bibliotheque.apiservice.request;

import com.bibliotheque.apiservice.enums.RoleUser;

import lombok.Data;

@Data
public class UserRequest {
  private String nom;

  private String email;

  private String password;

  private RoleUser role;

  private boolean deleted = false;
}
