package com.bibliotheque.apiservice.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.bibliotheque.apiservice.dto.EmpruntDTO;
import com.bibliotheque.apiservice.entity.Emprunt;
import com.bibliotheque.apiservice.entity.Livre;
import com.bibliotheque.apiservice.entity.User;
import com.bibliotheque.apiservice.enums.EmpruntStatus;
import com.bibliotheque.apiservice.request.EmpruntRequest;

@Component
public class EmpruntMapper {
  public Emprunt toEntity(EmpruntRequest request, User emprunteur, Livre livre) {
    Emprunt emprunt = new Emprunt();
    emprunt.setEmprunteur(emprunteur);
    emprunt.setLivre(livre);
    emprunt.setDateEmprunt(LocalDate.now());
    emprunt.setDateRetourPrevue(request.getDateRetourPrevue());
    emprunt.setStatut(EmpruntStatus.EN_COURS);
    return emprunt;
  }

  public EmpruntDTO toDTO(Emprunt emprunt) {
    EmpruntDTO dto = new EmpruntDTO();
    dto.setId(emprunt.getId());
    dto.setEmprunteurId(emprunt.getEmprunteur().getId());
    dto.setEmprunteurNom(emprunt.getEmprunteur().getNom());
    dto.setLivreId(emprunt.getLivre().getId());
    dto.setLivreTitre(emprunt.getLivre().getTitre());
    dto.setDateEmprunt(emprunt.getDateEmprunt());
    dto.setDateRetourPrevue(emprunt.getDateRetourPrevue());
    dto.setStatut(emprunt.getStatut().name());
    return dto;
  }
}
