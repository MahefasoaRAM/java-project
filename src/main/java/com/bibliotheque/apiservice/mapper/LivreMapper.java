package com.bibliotheque.apiservice.mapper;

import org.springframework.stereotype.Component;

import com.bibliotheque.apiservice.dto.LivreDTO;
import com.bibliotheque.apiservice.entity.Livre;
import com.bibliotheque.apiservice.request.LivreRequest;

@Component
public class LivreMapper {
  public Livre toEntity(LivreRequest livreRequest) {
    Livre livre = new Livre();
    livre.setTitre(livreRequest.getTitre());
    livre.setAuteur(livreRequest.getAuteur());
    livre.setCategorie(livreRequest.getCategorie());
    livre.setDisponible(true);
    return livre;
  }

  public LivreDTO toDTO(Livre livre) {
    LivreDTO livreDTO = new LivreDTO();
    livreDTO.setId(livre.getId());
    livreDTO.setTitre(livre.getTitre());
    livreDTO.setAuteur(livre.getAuteur());
    livreDTO.setCategorie(livre.getCategorie());
    livreDTO.setDisponible(livre.isDisponible());
    livreDTO.setDeleted(livre.isDeleted());
    return livreDTO;
  }
}
