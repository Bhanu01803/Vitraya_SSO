package org.example.vitraya_sso_token.repository;

import org.example.vitraya_sso_token.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
 
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByMobileNumberAndUsedFalse(String mobileNumber);
} 