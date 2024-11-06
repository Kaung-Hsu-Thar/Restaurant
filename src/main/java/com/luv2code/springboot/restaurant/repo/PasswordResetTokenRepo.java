package com.luv2code.springboot.restaurant.repo;

import com.luv2code.springboot.restaurant.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByEmailAndToken(String email, String token);
}
