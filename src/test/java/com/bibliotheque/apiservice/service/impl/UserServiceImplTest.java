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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bibliotheque.apiservice.dto.UserDTO;
import com.bibliotheque.apiservice.entity.User;
import com.bibliotheque.apiservice.enums.RoleUser;
import com.bibliotheque.apiservice.exception.DataConflictException;
import com.bibliotheque.apiservice.exception.ResourceNotFoundException;
import com.bibliotheque.apiservice.mapper.UserMapper;
import com.bibliotheque.apiservice.repository.UserRepository;
import com.bibliotheque.apiservice.request.UserRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;
  private UserRequest request;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .id(1L)
        .nom("Jean Dupont")
        .email("jean@example.com")
        .password("encodedSecret")
        .role(RoleUser.USER)
        .deleted(false)
        .build();

    request = new UserRequest();
    request.setNom("Jean Dupont");
    request.setEmail("jean@example.com");
    request.setPassword("secret123");
    request.setRole(RoleUser.USER);
  }

  @Test
  void createUser_devraitReussir_quandEmailUnique() {
    UserDTO expectedDto = new UserDTO();
    expectedDto.setId(1L);
    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(userMapper.toEntity(request, RoleUser.USER)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toDTO(user)).thenReturn(expectedDto);

    UserDTO result = userService.createUser(request, RoleUser.USER);

    assertThat(result).isEqualTo(expectedDto);
  }

  @Test
  void createUser_devraitEchouer_quandEmailDejaExistant() {
    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> userService.createUser(request, RoleUser.USER))
        .isInstanceOf(DataConflictException.class)
        .hasMessageContaining("Email already exists");

    verify(userRepository, never()).save(any());
  }

  @Test
  void getAllUsers_devraitRetournerLaListeMappee() {
    UserDTO dto = new UserDTO();
    dto.setId(1L);
    when(userRepository.findByDeletedFalse()).thenReturn(List.of(user));
    when(userMapper.toDTO(user)).thenReturn(dto);

    List<UserDTO> result = userService.getAllUsers();

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getAllUsersByRole_devraitRetournerLaListeFiltree() {
    UserDTO dto = new UserDTO();
    dto.setId(1L);
    when(userRepository.findByRoleAndDeletedFalse(RoleUser.USER)).thenReturn(List.of(user));
    when(userMapper.toDTO(user)).thenReturn(dto);

    List<UserDTO> result = userService.getAllUsersByRole(RoleUser.USER);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getUserById_devraitEchouer_quandUtilisateurInconnu() {
    when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserById(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void getUserByEmail_devraitEchouer_quandUtilisateurInconnu() {
    when(userRepository.findByEmail("inconnu@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserByEmail("inconnu@example.com"))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void updateUser_devraitMettreAJourNomEtEmail() {
    UserRequest update = new UserRequest();
    update.setNom("Nouveau Nom");
    update.setEmail("nouveau@example.com");
    UserDTO expectedDto = new UserDTO();
    expectedDto.setId(1L);

    when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toDTO(user)).thenReturn(expectedDto);

    UserDTO result = userService.updateUser(1L, update);

    assertThat(result).isEqualTo(expectedDto);
    assertThat(user.getNom()).isEqualTo("Nouveau Nom");
    assertThat(user.getEmail()).isEqualTo("nouveau@example.com");
  }

  @Test
  void updateUser_devraitReencoderMotDePasse_quandDifferent() {
    UserRequest update = new UserRequest();
    update.setPassword("nouveauMotDePasse");

    when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("nouveauMotDePasse", user.getPassword())).thenReturn(false);
    when(passwordEncoder.encode("nouveauMotDePasse")).thenReturn("encodedNouveauMotDePasse");
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toDTO(user)).thenReturn(new UserDTO());

    userService.updateUser(1L, update);

    assertThat(user.getPassword()).isEqualTo("encodedNouveauMotDePasse");
  }

  @Test
  void updateUser_devraitEchouer_quandUtilisateurInconnu() {
    when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.updateUser(99L, request))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void deleteUser_devraitMarquerCommeSupprime() {
    when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));

    userService.deleteUser(1L);

    assertThat(user.isDeleted()).isTrue();
    verify(userRepository).save(user);
  }

  @Test
  void deleteUser_devraitEchouer_quandUtilisateurInconnu() {
    when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.deleteUser(99L))
        .isInstanceOf(ResourceNotFoundException.class);

    verify(userRepository, never()).save(any());
  }
}
