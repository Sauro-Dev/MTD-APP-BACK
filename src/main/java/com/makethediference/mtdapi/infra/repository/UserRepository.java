package com.makethediference.mtdapi.infra.repository;

import com.makethediference.mtdapi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
