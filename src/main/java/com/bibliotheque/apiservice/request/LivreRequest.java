package com.bibliotheque.apiservice.request;

import lombok.Data;

@Data
public class LivreRequest {
  private String titre;

  private String auteur;

  private String categorie;
}
