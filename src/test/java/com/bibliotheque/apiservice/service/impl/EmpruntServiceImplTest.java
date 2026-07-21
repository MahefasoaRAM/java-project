package com.bibliotheque.apiservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bibliotheque.apiservice.dto.EmpruntDTO;
import com.bibliotheque.apiservice.entity.Emprunt;
import com.bibliotheque.apiservice.entity.Livre;
import com.bibliotheque.apiservice.entity.User;
import com.bibliotheque.apiservice.enums.EmpruntStatus;
import com.bibliotheque.apiservice.enums.RoleUser;
import com.bibliotheque.apiservice.exception.DataConflictException;
import com.bibliotheque.apiservice.exception.ResourceNotFoundException;
import com.bibliotheque.apiservice.mapper.EmpruntMapper;
import com.bibliotheque.apiservice.repository.EmpruntRepository;
import com.bibliotheque.apiservice.repository.LivreRepository;
import com.bibliotheque.apiservice.repository.UserRepository;
import com.bibliotheque.apiservice.request.EmpruntRequest;

/**
 * Tests unitaires du service d'emprunt (TDD - fonctionnalité "Emprunter un livre").
 * Cas couverts : emprunt réussi, emprunt refusé (livre indisponible),
 * retour de livre, ainsi que les cas d'erreur associés.
 */
@ExtendWith(MockitoExtension.class)
class EmpruntServiceImplTest {

  @Mock
  private EmpruntRepository empruntRepository;

  @Mock
  private LivreRepository livreRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EmpruntMapper empruntMapper;

  @InjectMocks
  private EmpruntServiceImpl empruntService;

  private User user;
  private Livre livre;
  private EmpruntRequest request;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .id(1L)
        .nom("Jean Dupont")
        .email("jean@example.com")
        .password("encoded")
        .role(RoleUser.USER)
        .deleted(false)
        .build();

    livre = Livre.builder()
        .id(10L)
        .titre("Le Petit Prince")
        .auteur("Antoine de Saint-Exupéry")
        .categorie("Littérature")
        .disponible(true)
        .deleted(false)
        .build();

    request = new EmpruntRequest();
    request.setLivreId(livre.getId());
    request.setDateRetourPrevue(LocalDate.now().plusDays(14));
  }

  @Test
  void emprunterLivre_devraitReussir_quandLivreDisponible() {
    Emprunt emprunt = Emprunt.builder()
        .emprunteur(user)
        .livre(livre)
        .dateEmprunt(LocalDate.now())
        .dateRetourPrevue(request.getDateRetourPrevue())
        .statut(EmpruntStatus.EN_COURS)
        .build();
    Emprunt savedEmprunt = Emprunt.builder()
        .id(100L)
        .emprunteur(user)
        .livre(livre)
        .dateEmprunt(emprunt.getDateEmprunt())
        .dateRetourPrevue(emprunt.getDateRetourPrevue())
        .statut(EmpruntStatus.EN_COURS)
        .build();
    EmpruntDTO expectedDto = new EmpruntDTO();
    expectedDto.setId(100L);
    expectedDto.setStatut("EN_COURS");

    when(userRepository.findByIdAndDeletedFalse(user.getId())).thenReturn(Optional.of(user));
    when(livreRepository.findByIdAndDeletedFalse(livre.getId())).thenReturn(Optional.of(livre));
    when(empruntMapper.toEntity(request, user, livre)).thenReturn(emprunt);
    when(empruntRepository.save(emprunt)).thenReturn(savedEmprunt);
    when(empruntMapper.toDTO(savedEmprunt)).thenReturn(expectedDto);

    EmpruntDTO result = empruntService.emprunterLivre(request, user.getId());

    assertThat(result).isEqualTo(expectedDto);
    assertThat(livre.isDisponible()).isFalse();
    verify(livreRepository).save(livre);
    verify(empruntRepository).save(emprunt);
  }

  @Test
  void emprunterLivre_devraitEchouer_quandLivreIndisponible() {
    livre.setDisponible(false);
    when(userRepository.findByIdAndDeletedFalse(user.getId())).thenReturn(Optional.of(user));
    when(livreRepository.findByIdAndDeletedFalse(livre.getId())).thenReturn(Optional.of(livre));

    assertThatThrownBy(() -> empruntService.emprunterLivre(request, user.getId()))
        .isInstanceOf(DataConflictException.class)
        .hasMessageContaining("Book is not available");

    verify(livreRepository, never()).save(any());
    verify(empruntRepository, never()).save(any());
  }

  @Test
  void emprunterLivre_devraitEchouer_quandUtilisateurInconnu() {
    when(userRepository.findByIdAndDeletedFalse(user.getId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> empruntService.emprunterLivre(request, user.getId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("User not found");

    verify(livreRepository, never()).findByIdAndDeletedFalse(any());
    verify(empruntRepository, never()).save(any());
  }

  @Test
  void emprunterLivre_devraitEchouer_quandLivreInconnu() {
    when(userRepository.findByIdAndDeletedFalse(user.getId())).thenReturn(Optional.of(user));
    when(livreRepository.findByIdAndDeletedFalse(livre.getId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> empruntService.emprunterLivre(request, user.getId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Book not found");

    verify(empruntRepository, never()).save(any());
  }

  @Test
  void retournerLivre_devraitReussir_quandEmpruntEnCours() {
    Emprunt emprunt = Emprunt.builder()
        .id(100L)
        .emprunteur(user)
        .livre(livre)
        .dateEmprunt(LocalDate.now().minusDays(5))
        .dateRetourPrevue(LocalDate.now().plusDays(9))
        .statut(EmpruntStatus.EN_COURS)
        .build();
    livre.setDisponible(false);
    EmpruntDTO expectedDto = new EmpruntDTO();
    expectedDto.setId(100L);
    expectedDto.setStatut("TERMINE");

    when(empruntRepository.findByIdAndStatut(100L, EmpruntStatus.EN_COURS)).thenReturn(Optional.of(emprunt));
    when(empruntMapper.toDTO(emprunt)).thenReturn(expectedDto);

    EmpruntDTO result = empruntService.retournerLivre(100L);

    assertThat(result).isEqualTo(expectedDto);
    assertThat(emprunt.getStatut()).isEqualTo(EmpruntStatus.TERMINE);
    assertThat(livre.isDisponible()).isTrue();
    verify(empruntRepository).save(emprunt);
    verify(livreRepository).save(livre);
  }

  @Test
  void retournerLivre_devraitEchouer_quandEmpruntNonTrouveOuDejaTermine() {
    when(empruntRepository.findByIdAndStatut(100L, EmpruntStatus.EN_COURS)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> empruntService.retournerLivre(100L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Active loan not found");

    verify(livreRepository, never()).save(any());
  }

  @Test
  void getEmpruntsEnCours_devraitRetournerLaListeMappee() {
    Emprunt emprunt = Emprunt.builder().id(1L).emprunteur(user).livre(livre).statut(EmpruntStatus.EN_COURS).build();
    EmpruntDTO dto = new EmpruntDTO();
    dto.setId(1L);

    when(empruntRepository.findByStatut(EmpruntStatus.EN_COURS)).thenReturn(List.of(emprunt));
    when(empruntMapper.toDTO(emprunt)).thenReturn(dto);

    List<EmpruntDTO> result = empruntService.getEmpruntsEnCours();

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getAllEmprunts_devraitRetournerTousLesEmpruntsMappes() {
    Emprunt emprunt = Emprunt.builder().id(1L).emprunteur(user).livre(livre).statut(EmpruntStatus.TERMINE).build();
    EmpruntDTO dto = new EmpruntDTO();
    dto.setId(1L);

    when(empruntRepository.findAll()).thenReturn(List.of(emprunt));
    when(empruntMapper.toDTO(emprunt)).thenReturn(dto);

    List<EmpruntDTO> result = empruntService.getAllEmprunts();

    assertThat(result).containsExactly(dto);
    verify(empruntRepository, times(1)).findAll();
  }
}
