package org.example.vitraya_sso_token.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vitraya_sso_token.model.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;  

import java.util.HashMap;
import java.util.Map;

@Service
public class SsoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SsoService.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SessionService sessionService;

    public Map<String, Object> checkTokenSession(HttpServletRequest request, String platform) {
        String accessToken = getSSOAccessToken(request);
        String refreshToken = getRefreshTokenCookie(request);
        String mobileNumber = null;
        boolean valid = false;

        // Extract additional headers for downstream SSO API calls
        Map<String, String> additionalHeaders = new HashMap<>();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()) {
            xForwardedFor = request.getRemoteAddr();
        }
        additionalHeaders.put("X-Forwarded-For", xForwardedFor);

        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            userAgent = "Unknown";
        }
        additionalHeaders.put("User-Agent", userAgent);

        String timeZone = request.getHeader("Time-Zone");
        if (timeZone != null && !timeZone.isEmpty()) {
            additionalHeaders.put("Time-Zone", timeZone);
        }

        // Step 1: Try to validate access token
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                mobileNumber = jwtService.extractUsername(accessToken);
                valid = (mobileNumber != null && jwtService.validateToken(accessToken, mobileNumber));
                LOGGER.info("Access token validation result: {}", valid);
            } catch (Exception e) {
                LOGGER.warn("Invalid access token: {}", e.getMessage());
            }
        }

        // Step 2: Try refresh token if access token is invalid
        if (!valid && refreshToken != null && !refreshToken.isEmpty()) {
            try {
                mobileNumber = jwtService.extractUsername(refreshToken);
                String newToken = jwtService.refreshToken(refreshToken);
                if (mobileNumber != null && newToken != null && jwtService.validateToken(newToken, mobileNumber)) {
                    accessToken = newToken;
                    valid = true;
                    LOGGER.info("Token refreshed successfully for user: {}", mobileNumber);
                }
            } catch (Exception e) {
                LOGGER.warn("Invalid refresh token: {}", e.getMessage());
            }
        }

        // Step 3: If no valid token found, return null (session not found)
        if (!valid) {
            LOGGER.warn("No valid token found for session check");
            return null;
        }

        // Step 4: Create session data for valid user
        try {
            SessionData sessionData = sessionService.createSessionData(mobileNumber);
            
            // Set the actual tokens in the session data's WebToken
            if (sessionData.getWebtoken() != null) {
                sessionData.getWebtoken().setToken(accessToken);
                sessionData.getWebtoken().setRefreshToken(refreshToken);
            }
            
            Map<String, Object> result = convertSessionDataToMap(sessionData);
            result.put("additionalHeaders", additionalHeaders); // Attach for downstream use
            
            LOGGER.info("Session data created successfully for user: {}", mobileNumber);
            return result;
        } catch (Exception e) {
            LOGGER.error("Error creating session data: {}", e.getMessage(), e);
            return null;
        }
    }

    private Map<String, Object> convertSessionDataToMap(SessionData sessionData) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("data", sessionData.getData());
        userData.put("corporate", sessionData.getCorporate());
        userData.put("webtoken", sessionData.getWebtoken());
        userData.put("id", sessionData.getId());
        userData.put("userid", sessionData.getUserid());
        userData.put("lastactivetime", sessionData.getLastactivetime());
        return userData;
    }

    public String getSSOAccessToken(HttpServletRequest request) {
        // Try header first, then cookies ("token" or "session")
        String token = request.getHeader("partner_accessToken");
        if (token == null || token.isEmpty()) {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName()) || "session".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return token;
    }

    public String getRefreshTokenCookie(HttpServletRequest request) {
        // Check both camelCase and lowercase header names
        String token = request.getHeader("partner_refreshToken");
        if (token == null || token.isEmpty()) {
            token = request.getHeader("partner_refreshtoken");
        }
        if ((token == null || token.isEmpty()) && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("partner_refreshToken".equals(cookie.getName()) || "partner_refreshtoken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return token;
    }
}