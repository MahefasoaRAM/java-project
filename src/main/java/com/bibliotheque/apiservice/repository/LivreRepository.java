package com.bibliotheque.apiservice.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.apiservice.entity.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
  Optional<Livre> findByTitre(String titre);

  Optional<Livre> findByIdAndDeletedFalse(Long id);

  List<Livre> findByAuteurIgnoreCase(String auteur);

  List<Livre> findByCategorieIgnoreCase(String categorie);
}
