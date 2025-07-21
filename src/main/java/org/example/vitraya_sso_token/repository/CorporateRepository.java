package org.example.vitraya_sso_token.repository;

import org.example.vitraya_sso_token.model.Corporate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CorporateRepository extends JpaRepository<Corporate, Long> {
    Optional<Corporate> findByHospitalCode(String hospitalCode);
} 