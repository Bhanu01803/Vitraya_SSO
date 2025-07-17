package org.example.vitraya_sso_token.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    // In-memory storage for OTPs (in production, use Redis or database)
    private final Map<String, OtpData> otpStorage = new HashMap<>();

    public String generateOtp(String mobileNumber) {
        String otp = generateRandomOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        otpStorage.put(mobileNumber, new OtpData(otp, expiryTime));
        System.out.println("Generated OTP for " + mobileNumber + ": " + otp + " (expires: " + expiryTime + ")");
        System.out.println("Current OTP storage size: " + otpStorage.size());

        // In production, send OTP via SMS service
        System.out.println("OTP for " + mobileNumber + ": " + otp);

        return otp;
    }

    public boolean validateOtp(String mobileNumber, String otp) {
        System.out.println("Validating OTP for " + mobileNumber + " with OTP: " + otp);
        System.out.println("Current OTP storage size: " + otpStorage.size());
        
        OtpData otpData = otpStorage.get(mobileNumber);
        System.out.println("Found OTP data: " + (otpData != null ? "yes" : "no"));

        if (otpData == null) {
            System.out.println("No OTP data found for mobile: " + mobileNumber);
            return false;
        }

        if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
            System.out.println("OTP expired for mobile: " + mobileNumber);
            otpStorage.remove(mobileNumber);
            return false;
        }

        boolean isValid = otpData.getOtp().equals(otp);
        System.out.println("OTP comparison result: " + isValid + " (stored: " + otpData.getOtp() + ", provided: " + otp + ")");
        
        if (isValid) {
            otpStorage.remove(mobileNumber);
            System.out.println("OTP validated successfully, removed from storage");
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

    private static class OtpData {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}