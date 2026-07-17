package com.bibliotheque.apiservice.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.apiservice.entity.User;
import com.bibliotheque.apiservice.enums.RoleUser;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  List<User> findByDeletedFalse();

  List<User> findByRoleAndDeletedFalse(RoleUser role);

  Optional<User> findByIdAndDeletedFalse(Long id);
}
