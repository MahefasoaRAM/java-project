package com.bibliotheque.apiservice.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bibliotheque.apiservice.dto.LivreDTO;
import com.bibliotheque.apiservice.entity.Livre;
import com.bibliotheque.apiservice.enums.EmpruntStatus;
import com.bibliotheque.apiservice.exception.DataConflictException;
import com.bibliotheque.apiservice.exception.ResourceNotFoundException;
import com.bibliotheque.apiservice.mapper.LivreMapper;
import com.bibliotheque.apiservice.repository.EmpruntRepository;
import com.bibliotheque.apiservice.repository.LivreRepository;
import com.bibliotheque.apiservice.request.LivreRequest;
import com.bibliotheque.apiservice.service.LivreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivreServiceImpl implements LivreService {
  private final LivreRepository livreRepository;
  private final EmpruntRepository empruntRepository;
  private final LivreMapper livreMapper;

  @Override
  public LivreDTO createLivre(LivreRequest livreRequest) {
    if (livreRequest.getTitre() == null || livreRequest.getTitre().isEmpty()) {
      throw new com.bibliotheque.apiservice.exception.BadRequestException("Title is required");
    }
    if (livreRequest.getAuteur() == null || livreRequest.getAuteur().isEmpty()) {
      throw new com.bibliotheque.apiservice.exception.BadRequestException("Author is required");
    }
    if (livreRepository.findByTitre(livreRequest.getTitre()).isPresent()) {
      throw new DataConflictException("Book already exists with title: " + livreRequest.getTitre());
    }
    Livre livre = livreMapper.toEntity(livreRequest);
    Livre savedLivre = livreRepository.save(livre);
    return livreMapper.toDTO(savedLivre);
  }

  @Override
  public List<LivreDTO> getAllLivres() {
    List<Livre> livres = livreRepository.findAll();
    return livres.stream().map(livreMapper::toDTO).toList();
  }

  @Override
  public LivreDTO getLivreById(Long id) {
    Livre livre = livreRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    return livreMapper.toDTO(livre);
  }

  @Override
  public LivreDTO getLivreByTitre(String titre) {
    Livre livre = livreRepository.findByTitre(titre)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found with title: " + titre));
    return livreMapper.toDTO(livre);
  }

  @Override
  public List<LivreDTO> getLivresByAuteur(String auteur) {
    List<Livre> livres = livreRepository.findByAuteurIgnoreCase(auteur);
    return livres.stream().map(livreMapper::toDTO).toList();
  }

  @Override
  public List<LivreDTO> getLivresByCategorie(String categorie) {
    List<Livre> livres = livreRepository.findByCategorieIgnoreCase(categorie);
    return livres.stream().map(livreMapper::toDTO).toList();
  }

  @Override
  public LivreDTO updateLivre(Long id, LivreRequest livreRequest) {
    Livre existingLivre = livreRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    if (livreRequest.getTitre() != null && !livreRequest.getTitre().isEmpty()
        && !livreRequest.getTitre().equals(existingLivre.getTitre())) {
      if (livreRepository.findByTitre(livreRequest.getTitre()).isPresent()) {
        throw new DataConflictException("Book already exists with title: " + livreRequest.getTitre());
      }
      existingLivre.setTitre(livreRequest.getTitre());
    }
    if (livreRequest.getAuteur() != null && !livreRequest.getAuteur().isEmpty()
        && !livreRequest.getAuteur().equals(existingLivre.getAuteur())) {
      existingLivre.setAuteur(livreRequest.getAuteur());
    }
    if (livreRequest.getCategorie() != null && !livreRequest.getCategorie().isEmpty()
        && !livreRequest.getCategorie().equals(existingLivre.getCategorie())) {
      existingLivre.setCategorie(livreRequest.getCategorie());
    }
    Livre savedLivre = livreRepository.save(existingLivre);
    return livreMapper.toDTO(savedLivre);
  }

  @Override
  public void deleteLivre(Long id) {
    Livre existingLivre = livreRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    if (empruntRepository.existsByLivreIdAndStatut(id, EmpruntStatus.EN_COURS)) {
      throw new DataConflictException("Cannot delete book with active loans: " + existingLivre.getTitre());
    }
    existingLivre.setDeleted(true);
    livreRepository.save(existingLivre);
  }
}
