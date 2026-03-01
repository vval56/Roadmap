package com.example.roadmap.repository;

import com.example.roadmap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for users.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
