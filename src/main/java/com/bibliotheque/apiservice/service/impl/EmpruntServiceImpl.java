package com.bibliotheque.apiservice.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.apiservice.dto.EmpruntDTO;
import com.bibliotheque.apiservice.entity.Emprunt;
import com.bibliotheque.apiservice.entity.Livre;
import com.bibliotheque.apiservice.entity.User;
import com.bibliotheque.apiservice.enums.EmpruntStatus;
import com.bibliotheque.apiservice.exception.DataConflictException;
import com.bibliotheque.apiservice.exception.ResourceNotFoundException;
import com.bibliotheque.apiservice.mapper.EmpruntMapper;
import com.bibliotheque.apiservice.repository.EmpruntRepository;
import com.bibliotheque.apiservice.repository.LivreRepository;
import com.bibliotheque.apiservice.repository.UserRepository;
import com.bibliotheque.apiservice.request.EmpruntRequest;
import com.bibliotheque.apiservice.service.EmpruntService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpruntServiceImpl implements EmpruntService {
  private final EmpruntRepository empruntRepository;
  private final LivreRepository livreRepository;
  private final UserRepository userRepository;
  private final EmpruntMapper empruntMapper;

  @Override
  @Transactional
  public EmpruntDTO emprunterLivre(EmpruntRequest request, Long userIdAuth) {
    User emprunteur = userRepository.findByIdAndDeletedFalse(userIdAuth)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userIdAuth));

    Livre livre = livreRepository.findByIdAndDeletedFalse(request.getLivreId())
        .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getLivreId()));

    if (!livre.isDisponible()) {
      throw new DataConflictException("Book is not available: " + livre.getTitre());
    }

    livre.setDisponible(false);
    livreRepository.save(livre);

    Emprunt emprunt = empruntMapper.toEntity(request, emprunteur, livre);
    Emprunt savedEmprunt = empruntRepository.save(emprunt);
    return empruntMapper.toDTO(savedEmprunt);
  }

  @Override
  @Transactional
  public EmpruntDTO retournerLivre(Long empruntId) {
    Emprunt emprunt = empruntRepository.findByIdAndStatut(empruntId, EmpruntStatus.EN_COURS)
        .orElseThrow(() -> new ResourceNotFoundException("Active loan not found with id: " + empruntId));

    emprunt.setStatut(EmpruntStatus.TERMINE);
    empruntRepository.save(emprunt);

    Livre livre = emprunt.getLivre();
    livre.setDisponible(true);
    livreRepository.save(livre);

    return empruntMapper.toDTO(emprunt);
  }

  @Override
  public List<EmpruntDTO> getEmpruntsEnCours() {
    List<Emprunt> emprunts = empruntRepository.findByStatut(EmpruntStatus.EN_COURS);
    return emprunts.stream().map(empruntMapper::toDTO).toList();
  }

  @Override
  public List<EmpruntDTO> getAllEmprunts() {
    List<Emprunt> emprunts = empruntRepository.findAll();
    return emprunts.stream().map(empruntMapper::toDTO).toList();
  }
}
