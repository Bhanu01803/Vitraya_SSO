package org.example.vitraya_sso_token.repository;

import org.example.vitraya_sso_token.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
} 