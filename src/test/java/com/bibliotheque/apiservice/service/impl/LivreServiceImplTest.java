package com.bibliotheque.apiservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bibliotheque.apiservice.dto.LivreDTO;
import com.bibliotheque.apiservice.entity.Livre;
import com.bibliotheque.apiservice.enums.EmpruntStatus;
import com.bibliotheque.apiservice.exception.BadRequestException;
import com.bibliotheque.apiservice.exception.DataConflictException;
import com.bibliotheque.apiservice.exception.ResourceNotFoundException;
import com.bibliotheque.apiservice.mapper.LivreMapper;
import com.bibliotheque.apiservice.repository.EmpruntRepository;
import com.bibliotheque.apiservice.repository.LivreRepository;
import com.bibliotheque.apiservice.request.LivreRequest;

@ExtendWith(MockitoExtension.class)
class LivreServiceImplTest {

  @Mock
  private LivreRepository livreRepository;

  @Mock
  private EmpruntRepository empruntRepository;

  @Mock
  private LivreMapper livreMapper;

  @InjectMocks
  private LivreServiceImpl livreService;

  private Livre livre;
  private LivreRequest request;

  @BeforeEach
  void setUp() {
    livre = Livre.builder()
        .id(1L)
        .titre("Le Petit Prince")
        .auteur("Antoine de Saint-Exupéry")
        .categorie("Littérature")
        .disponible(true)
        .deleted(false)
        .build();

    request = new LivreRequest();
    request.setTitre("Le Petit Prince");
    request.setAuteur("Antoine de Saint-Exupéry");
    request.setCategorie("Littérature");
  }

  @Test
  void createLivre_devraitReussir_quandTitreUnique() {
    LivreDTO expectedDto = new LivreDTO();
    expectedDto.setId(1L);
    when(livreRepository.findByTitre(request.getTitre())).thenReturn(Optional.empty());
    when(livreMapper.toEntity(request)).thenReturn(livre);
    when(livreRepository.save(livre)).thenReturn(livre);
    when(livreMapper.toDTO(livre)).thenReturn(expectedDto);

    LivreDTO result = livreService.createLivre(request);

    assertThat(result).isEqualTo(expectedDto);
  }

  @Test
  void createLivre_devraitEchouer_quandTitreManquant() {
    request.setTitre("");

    assertThatThrownBy(() -> livreService.createLivre(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Title is required");

    verify(livreRepository, never()).save(any());
  }

  @Test
  void createLivre_devraitEchouer_quandAuteurManquant() {
    request.setAuteur(null);

    assertThatThrownBy(() -> livreService.createLivre(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Author is required");

    verify(livreRepository, never()).save(any());
  }

  @Test
  void createLivre_devraitEchouer_quandTitreDejaExistant() {
    when(livreRepository.findByTitre(request.getTitre())).thenReturn(Optional.of(livre));

    assertThatThrownBy(() -> livreService.createLivre(request))
        .isInstanceOf(DataConflictException.class)
        .hasMessageContaining("already exists");

    verify(livreRepository, never()).save(any());
  }

  @Test
  void getLivreById_devraitRetournerLeLivre_quandTrouve() {
    LivreDTO expectedDto = new LivreDTO();
    expectedDto.setId(1L);
    when(livreRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(livre));
    when(livreMapper.toDTO(livre)).thenReturn(expectedDto);

    LivreDTO result = livreService.getLivreById(1L);

    assertThat(result).isEqualTo(expectedDto);
  }

  @Test
  void getLivreById_devraitEchouer_quandLivreInconnu() {
    when(livreRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> livreService.getLivreById(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void getLivreByTitre_devraitEchouer_quandLivreInconnu() {
    when(livreRepository.findByTitre("Inconnu")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> livreService.getLivreByTitre("Inconnu"))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void getLivresByAuteur_devraitRetournerLaListeMappee() {
    LivreDTO dto = new LivreDTO();
    dto.setId(1L);
    when(livreRepository.findByAuteurIgnoreCase("Antoine de Saint-Exupéry")).thenReturn(List.of(livre));
    when(livreMapper.toDTO(livre)).thenReturn(dto);

    List<LivreDTO> result = livreService.getLivresByAuteur("Antoine de Saint-Exupéry");

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getLivresByCategorie_devraitRetournerLaListeMappee() {
    LivreDTO dto = new LivreDTO();
    dto.setId(1L);
    when(livreRepository.findByCategorieIgnoreCase("Littérature")).thenReturn(List.of(livre));
    when(livreMapper.toDTO(livre)).thenReturn(dto);

    List<LivreDTO> result = livreService.getLivresByCategorie("Littérature");

    assertThat(result).containsExactly(dto);
  }

  @Test
  void updateLivre_devraitMettreAJourLesChamps() {
    LivreRequest update = new LivreRequest();
    update.setTitre("Nouveau Titre");
    update.setAuteur("Nouvel Auteur");
    update.setCategorie("Nouvelle Categorie");
    LivreDTO expectedDto = new LivreDTO();
    expectedDto.setId(1L);

    when(livreRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(livre));
    when(livreRepository.findByTitre("Nouveau Titre")).thenReturn(Optional.empty());
    when(livreRepository.save(livre)).thenReturn(livre);
    when(livreMapper.toDTO(livre)).thenReturn(expectedDto);

    LivreDTO result = livreService.updateLivre(1L, update);

    assertThat(result).isEqualTo(expectedDto);
    assertThat(livre.getTitre()).isEqualTo("Nouveau Titre");
    assertThat(livre.getAuteur()).isEqualTo("Nouvel Auteur");
    assertThat(livre.getCategorie()).isEqualTo("Nouvelle Categorie");
  }

  @Test
  void updateLivre_devraitEchouer_quandNouveauTitreDejaPris() {
    LivreRequest update = new LivreRequest();
    update.setTitre("Titre Existant");
    Livre autreLivre = Livre.builder().id(2L).titre("Titre Existant").build();

    when(livreRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(livre));
    when(livreRepository.findByTitre("Titre Existant")).thenReturn(Optional.of(autreLivre));

    assertThatThrownBy(() -> livreService.updateLivre(1L, update))
        .isInstanceOf(DataConflictException.class);

    verify(livreRepository, never()).save(any());
  }

  @Test
  void deleteLivre_devraitReussir_quandAucunEmpruntEnCours() {
    when(livreRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(livre));
    when(empruntRepository.existsByLivreIdAndStatut(1L, EmpruntStatus.EN_COURS)).thenReturn(false);

    livreService.deleteLivre(1L);

    assertThat(livre.isDeleted()).isTrue();
    verify(livreRepository).save(livre);
  }

  @Test
  void deleteLivre_devraitEchouer_quandEmpruntEnCours() {
    when(livreRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(livre));
    when(empruntRepository.existsByLivreIdAndStatut(1L, EmpruntStatus.EN_COURS)).thenReturn(true);

    assertThatThrownBy(() -> livreService.deleteLivre(1L))
        .isInstanceOf(DataConflictException.class)
        .hasMessageContaining("active loans");

    verify(livreRepository, never()).save(any());
  }

  @Test
  void deleteLivre_devraitEchouer_quandLivreInconnu() {
    when(livreRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> livreService.deleteLivre(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
