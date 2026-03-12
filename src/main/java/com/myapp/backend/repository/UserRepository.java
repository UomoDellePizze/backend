package com.myapp.backend.repository;

import com.myapp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository per gestire gli utenti nel database.
 * Estende JpaRepository quindi include tutti i metodi CRUD standard:
 *  - save, findById, findAll, delete, deleteById, existsById, count
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
}