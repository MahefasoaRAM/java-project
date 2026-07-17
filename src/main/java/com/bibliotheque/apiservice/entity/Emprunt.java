package com.bibliotheque.apiservice.entity;

import java.time.LocalDate;

import com.bibliotheque.apiservice.enums.EmpruntStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Emprunt {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emprunteur_id")
  private User emprunteur;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "livre_id")
  private Livre livre;

  private LocalDate dateEmprunt;

  private LocalDate dateRetourPrevue;

  @Enumerated(EnumType.STRING)
  private EmpruntStatus statut;
}
