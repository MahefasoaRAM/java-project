package com.bibliotheque.apiservice.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE livre SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Livre {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String titre;

  private String auteur;

  private String categorie;

  @Builder.Default
  private boolean disponible = true;

  @Builder.Default
  private boolean deleted = false;
}
