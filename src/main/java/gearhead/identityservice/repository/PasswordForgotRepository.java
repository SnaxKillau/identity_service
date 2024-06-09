package gearhead.identityservice.repository;

import gearhead.identityservice.entity.PasswordForgotToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordForgotRepository extends JpaRepository<PasswordForgotToken, Integer> {
    Optional<PasswordForgotToken> findByToken(String token);
}
