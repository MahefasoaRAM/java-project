package com.bibliotheque.apiservice.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.apiservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  List<User> findByDeletedFalse();

  List<User> findByRoleAndDeletedFalse(String role);

  Optional<User> findByIdAndDeletedFalse(Long id);
}
