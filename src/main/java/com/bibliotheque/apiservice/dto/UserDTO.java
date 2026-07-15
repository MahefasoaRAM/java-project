package com.bibliotheque.apiservice.dto;

import lombok.Data;

@Data
public class UserDTO {
  private Long id;

  private String nom;

  private String email;

  private String password;

  private String role;

  private boolean deleted = false;

  public boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
