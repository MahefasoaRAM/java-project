package com.bibliotheque.apiservice.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.bibliotheque.apiservice.enums.RoleUser;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE user SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nom;

  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private RoleUser role;

  @Builder.Default
  private boolean deleted = false;
}
