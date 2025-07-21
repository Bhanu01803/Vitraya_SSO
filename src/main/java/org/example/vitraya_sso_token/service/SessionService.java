package org.example.vitraya_sso_token.service;

import org.example.vitraya_sso_token.model.*;
import org.example.vitraya_sso_token.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorporateRepository corporateRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private WebTokenRepository webTokenRepository;

    public SessionData createSessionData(String mobileNumber) {
        // Fetch or create user
        User user = userRepository.findByMobileNumber(mobileNumber).orElseGet(() -> {
            User newUser = new User();
            newUser.setMobileNumber(mobileNumber);
            newUser.setEmail("abc@abc.com");
            newUser.setBlocked("false");
            newUser.setUserid(generateUserId());
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setNewUser(true);
            newUser.setLastLoginType("SSO");
            newUser.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(newUser);
        });

        // Update last login info
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginType("SSO");
        userRepository.save(user);

        // Fetch corporate
        Corporate corporate = corporateRepository.findByHospitalCode("HOS-3206")
                .orElseGet(() -> corporateRepository.findAll().stream().findFirst().orElse(null));

        // Create web token with access and refresh tokens
        WebToken webToken = new WebToken();
        webToken.setToken(jwtService.generateToken(mobileNumber));
        webToken.setRefreshToken(jwtService.generateRefreshToken(mobileNumber)); // Use the new method
        webToken.setExpiryAt("2000-01-01"); // This might represent the refresh token's expiry
        webTokenRepository.save(webToken);

        // Create session
        Session session = new Session();
        session.setUser(user);
        session.setCorporate(corporate);
        session.setWebToken(webToken);
        session.setLastActiveTime(new Date());
        sessionRepository.save(session);

        // Create response data
        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("createdat", user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")));
        userDataMap.put("isnewuser", user.isNewUser());
        userDataMap.put("lastlogintype", user.getLastLoginType());
        userDataMap.put("cellnumber", user.getMobileNumber());
        userDataMap.put("blocked", user.getBlocked());
        userDataMap.put("userid", user.getUserid());
        userDataMap.put("email", user.getEmail());

        SessionData sessionData = new SessionData();
        sessionData.setData(userDataMap);
        sessionData.setCorporate(corporate);
        
        // Convert WebToken to SessionData.WebToken
        SessionData.WebToken apiWebToken = new SessionData.WebToken();
        apiWebToken.setToken(webToken.getToken());
        apiWebToken.setRefreshToken(webToken.getRefreshToken());
        apiWebToken.setExpiryat(webToken.getExpiryAt());
        sessionData.setWebtoken(apiWebToken);
        
        sessionData.setId(session.getId() != null ? String.valueOf(session.getId()) : null);
        sessionData.setUserid(user.getUserid());
        sessionData.setLastactivetime(session.getLastActiveTime());

        return sessionData;
    }

    private String generateUserId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}