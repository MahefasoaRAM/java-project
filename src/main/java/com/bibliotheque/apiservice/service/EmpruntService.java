package com.bibliotheque.apiservice.service;

import java.util.List;

import com.bibliotheque.apiservice.dto.EmpruntDTO;
import com.bibliotheque.apiservice.request.EmpruntRequest;

public interface EmpruntService {
  EmpruntDTO emprunterLivre(EmpruntRequest request, Long userIdAuth);

  EmpruntDTO retournerLivre(Long empruntId);

  List<EmpruntDTO> getEmpruntsEnCours();

  List<EmpruntDTO> getAllEmprunts();
}
