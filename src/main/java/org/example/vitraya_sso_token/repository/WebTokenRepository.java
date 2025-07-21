package org.example.vitraya_sso_token.repository;

import org.example.vitraya_sso_token.model.WebToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WebTokenRepository extends JpaRepository<WebToken, Long> {
    Optional<WebToken> findByToken(String token);
} 