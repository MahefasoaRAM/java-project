package com.bibliotheque.apiservice.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.apiservice.entity.Emprunt;
import com.bibliotheque.apiservice.enums.EmpruntStatus;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
  List<Emprunt> findByStatut(EmpruntStatus statut);

  Optional<Emprunt> findByIdAndStatut(Long id, EmpruntStatus statut);

  boolean existsByLivreIdAndStatut(Long livreId, EmpruntStatus statut);
}
