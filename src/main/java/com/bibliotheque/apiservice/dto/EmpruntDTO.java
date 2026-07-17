package com.bibliotheque.apiservice.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmpruntDTO {
  private Long id;

  private Long emprunteurId;

  private String emprunteurNom;

  private Long livreId;

  private String livreTitre;

  private LocalDate dateEmprunt;

  private LocalDate dateRetourPrevue;

  private String statut;
}
