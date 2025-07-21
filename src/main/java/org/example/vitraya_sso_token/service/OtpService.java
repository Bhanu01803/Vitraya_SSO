package org.example.vitraya_sso_token.service;

import org.example.vitraya_sso_token.model.Otp;
import org.example.vitraya_sso_token.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    @Autowired
    private OtpRepository otpRepository;

    public String generateOtp(String mobileNumber) {
        String otp = generateRandomOtp();
        String otpHash = hashOtp(otp);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        // Find existing unused OTP for this mobile number, or create a new one.
        Otp otpEntity = otpRepository.findByMobileNumberAndUsedFalse(mobileNumber)
                .orElse(new Otp());
        
        // Update or set the properties
        otpEntity.setMobileNumber(mobileNumber);
        otpEntity.setOtpHash(otpHash);
        otpEntity.setPlainOtp(otp); // Store plain OTP for debugging
        otpEntity.setExpiryTime(expiryTime);
        otpEntity.setUsed(false); // Ensure it's marked as not used
        otpRepository.save(otpEntity);

        // In production, send OTP via SMS service
        System.out.println("OTP for " + mobileNumber + ": " + otp);

        return otp;
    }

    public boolean validateOtp(String mobileNumber, String otp) {
        Optional<Otp> otpOpt = otpRepository.findByMobileNumberAndUsedFalse(mobileNumber);
        if (otpOpt.isEmpty()) {
            System.out.println("No OTP data found for mobile: " + mobileNumber);
            return false;
        }

        Otp otpEntity = otpOpt.get();
        if (LocalDateTime.now().isAfter(otpEntity.getExpiryTime())) {
            System.out.println("OTP expired for mobile: " + mobileNumber);
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);
            return false;
        }

        boolean isValid = otpEntity.getOtpHash().equals(hashOtp(otp));
        System.out.println("OTP comparison result: " + isValid);
        
        if (isValid) {
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);
            System.out.println("OTP validated successfully, marked as used");
        }
        
        return isValid;
    }

    private String generateRandomOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing OTP", e);
        }
    }
}