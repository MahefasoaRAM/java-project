package com.bibliotheque.apiservice.request;

import lombok.Data;

@Data
public class UserRequest {
  private String nom;

  private String email;

  private String password;

  private String role;

  private boolean deleted = false;
}
