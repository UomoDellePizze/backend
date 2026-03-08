package com.myapp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myapp.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}