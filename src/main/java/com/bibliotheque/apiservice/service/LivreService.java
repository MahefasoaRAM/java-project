package com.bibliotheque.apiservice.service;

import java.util.List;

import com.bibliotheque.apiservice.dto.LivreDTO;
import com.bibliotheque.apiservice.request.LivreRequest;

public interface LivreService {
  LivreDTO createLivre(LivreRequest livreRequest);

  List<LivreDTO> getAllLivres();

  LivreDTO getLivreById(Long id);

  LivreDTO getLivreByTitre(String titre);

  List<LivreDTO> getLivresByAuteur(String auteur);

  List<LivreDTO> getLivresByCategorie(String categorie);

  LivreDTO updateLivre(Long id, LivreRequest livreRequest);

  void deleteLivre(Long id);
}
