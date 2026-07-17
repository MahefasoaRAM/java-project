package com.bibliotheque.apiservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bibliotheque.apiservice.dto.EmpruntDTO;
import com.bibliotheque.apiservice.request.EmpruntRequest;
import com.bibliotheque.apiservice.service.EmpruntService;
import com.bibliotheque.apiservice.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emprunts")
public class EmpruntController {
  private final EmpruntService empruntService;
  private final JwtService jwtService;

  private Long extractUserId(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    String jwt = authHeader.substring(7);
    return jwtService.extractAllClaims(jwt).get("userId", Long.class);
  }

  @PostMapping
  public ResponseEntity<EmpruntDTO> emprunterLivre(@RequestBody EmpruntRequest empruntRequest,
      HttpServletRequest request) {
    Long userId = extractUserId(request);
    EmpruntDTO emprunt = empruntService.emprunterLivre(empruntRequest, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(emprunt);
  }

  @PutMapping("/{id}/retour")
  public ResponseEntity<EmpruntDTO> retournerLivre(@PathVariable("id") Long id) {
    EmpruntDTO emprunt = empruntService.retournerLivre(id);
    return ResponseEntity.ok(emprunt);
  }

  @GetMapping
  public ResponseEntity<List<EmpruntDTO>> getAllEmprunts() {
    List<EmpruntDTO> emprunts = empruntService.getAllEmprunts();
    return ResponseEntity.ok(emprunts);
  }

  @GetMapping("/en-cours")
  public ResponseEntity<List<EmpruntDTO>> getEmpruntsEnCours() {
    List<EmpruntDTO> emprunts = empruntService.getEmpruntsEnCours();
    return ResponseEntity.ok(emprunts);
  }
}
