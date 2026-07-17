package com.bibliotheque.apiservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bibliotheque.apiservice.dto.LivreDTO;
import com.bibliotheque.apiservice.request.LivreRequest;
import com.bibliotheque.apiservice.service.LivreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/livres")
public class LivreController {
  private final LivreService livreService;

  @PostMapping(consumes = MediaType.ALL_VALUE)
  public ResponseEntity<LivreDTO> createLivre(@RequestBody LivreRequest livreRequest) {
    LivreDTO createdLivre = livreService.createLivre(livreRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdLivre);
  }

  @GetMapping
  public ResponseEntity<List<LivreDTO>> getAllLivres() {
    List<LivreDTO> livres = livreService.getAllLivres();
    return ResponseEntity.ok(livres);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LivreDTO> getLivreById(@PathVariable("id") Long id) {
    LivreDTO livre = livreService.getLivreById(id);
    return ResponseEntity.ok(livre);
  }

  @GetMapping("/titre/{titre}")
  public ResponseEntity<LivreDTO> getLivreByTitre(@PathVariable("titre") String titre) {
    LivreDTO livre = livreService.getLivreByTitre(titre);
    return ResponseEntity.ok(livre);
  }

  @GetMapping("/auteur/{auteur}")
  public ResponseEntity<List<LivreDTO>> getLivresByAuteur(@PathVariable("auteur") String auteur) {
    List<LivreDTO> livres = livreService.getLivresByAuteur(auteur);
    return ResponseEntity.ok(livres);
  }

  @GetMapping("/categorie/{categorie}")
  public ResponseEntity<List<LivreDTO>> getLivresByCategorie(@PathVariable("categorie") String categorie) {
    List<LivreDTO> livres = livreService.getLivresByCategorie(categorie);
    return ResponseEntity.ok(livres);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<LivreDTO> updateLivre(@PathVariable("id") Long id, @RequestBody LivreRequest livreRequest) {
    LivreDTO updatedLivre = livreService.updateLivre(id, livreRequest);
    return ResponseEntity.ok(updatedLivre);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLivre(@PathVariable("id") Long id) {
    livreService.deleteLivre(id);
    return ResponseEntity.noContent().build();
  }
}
