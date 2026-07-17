package com.bibliotheque.apiservice.dto;

import lombok.Data;

@Data
public class LivreDTO {
  private Long id;

  private String titre;

  private String auteur;

  private String categorie;

  private boolean disponible;

  private boolean deleted;
}
