package org.example.vitraya_sso_token.controller;

import jakarta.validation.Valid;
import org.example.vitraya_sso_token.model.ApiResponse;
import org.example.vitraya_sso_token.model.OtpRequest;
import org.example.vitraya_sso_token.model.OtpVerificationRequest;
import org.example.vitraya_sso_token.model.RestApiResponse;
import org.example.vitraya_sso_token.model.SessionData;
import org.example.vitraya_sso_token.model.ValidateTokenRequest;
import org.example.vitraya_sso_token.model.SsoApiCallResponse;
import org.example.vitraya_sso_token.service.JwtService;
import org.example.vitraya_sso_token.service.OtpService;
import org.example.vitraya_sso_token.service.SessionService;
import org.example.vitraya_sso_token.service.SsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8081"}, allowCredentials = "true")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SsoService ssoService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody OtpRequest request) {
        try {
            String otp = otpService.generateOtp(request.getMobileNumber());
            return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent successfully", "OTP sent to " + request.getMobileNumber()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<SessionData>> verifyOtp(@Valid @RequestBody OtpVerificationRequest request,
                                                              HttpServletResponse response) {
        try {
            System.out.println("Verifying OTP for mobile: " + request.getMobileNumber() + ", OTP: " + request.getOtp());

            boolean isValid = otpService.validateOtp(request.getMobileNumber(), request.getOtp());
            System.out.println("OTP validation result: " + isValid);

            if (isValid) {
                System.out.println("OTP is valid, creating session data...");
                SessionData sessionData = sessionService.createSessionData(request.getMobileNumber());


                response.setHeader("partner_accessToken", sessionData.getWebtoken().getToken());
                response.setHeader("partner_refreshToken", sessionData.getWebtoken().getRefreshToken());


                Cookie tokenCookie = new Cookie("partner_accessToken", sessionData.getWebtoken().getToken());
                tokenCookie.setPath("/");
                tokenCookie.setHttpOnly(false); // Angular needs to read this
                tokenCookie.setMaxAge(24 * 60 * 60); // 24 hours
                response.addCookie(tokenCookie);

                
                Cookie sessionCookie = new Cookie("session", sessionData.getWebtoken().getToken());
                sessionCookie.setPath("/");
                sessionCookie.setHttpOnly(true);
                sessionCookie.setMaxAge(24 * 60 * 60); // 24 hours
                response.addCookie(sessionCookie);

                // Set refresh token cookie for SSO compatibility
                Cookie refreshCookie = new Cookie("partner_refreshToken", sessionData.getWebtoken().getRefreshToken());
                refreshCookie.setPath("/");
                refreshCookie.setHttpOnly(false); // Angular needs to read this
                refreshCookie.setMaxAge(24 * 60 * 60); // 24 hours
                response.addCookie(refreshCookie);

                System.out.println("Session created successfully, returning response");
                System.out.println("Token set in cookie: " + sessionData.getWebtoken().getToken());
                return ResponseEntity.ok(new ApiResponse<>(true, sessionData, "OTP verified successfully"));
            } else {
                System.out.println("OTP is invalid");
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid OTP"));
            }
        } catch (Exception e) {
            System.err.println("Exception during OTP verification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to verify OTP: " + e.getMessage()));
        }
    }

    /**
     * POST /check/session
     * Required headers:
     *   - X-Client-Platform (required)
     *   - partner_accessToken (required, in header or cookie)
     *   - partner_refreshToken (required if access token expired, in header or cookie)
     * Optional headers:
     *   - X-Forwarded-For (client IP, fallback to request.getRemoteAddr())
     *   - User-Agent (fallback to 'Unknown')
     *   - Time-Zone (optional)
     *
     * All headers are passed to SsoService for downstream SSO API calls.
     */
    @PostMapping("/check/session")
    public RestApiResponse checkUser(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestHeader(value = "X-Client-Platform") String platform) {
        LOGGER.info("cookies - :" + Arrays.toString(request.getCookies()));
        try {
            // Validate required headers
            String accessToken = request.getHeader("partner_accessToken");
            if (accessToken == null || accessToken.isEmpty()) {
                LOGGER.warn("Missing partner_accessToken header");
                return RestApiResponse.buildFail("User session not found.", "TOKEN_EXPIRED");
            }

            // SsoService now handles all required/optional headers for SSO downstream calls
            Map<String, Object> userData = ssoService.checkTokenSession(request, platform);
            if (userData != null) {
                // Set refresh token in response header for UI if available
                Object webtokenObj = userData.get("webtoken");
                if (webtokenObj instanceof org.example.vitraya_sso_token.model.SessionData.WebToken) {
                    org.example.vitraya_sso_token.model.SessionData.WebToken webtoken = (org.example.vitraya_sso_token.model.SessionData.WebToken) webtokenObj;
                    response.setHeader("partner_refreshToken", webtoken.getRefreshToken());
                }
                // Ensure all required fields are present in response
                if (!userData.containsKey("data")) userData.put("data", null);
                if (!userData.containsKey("corporate")) userData.put("corporate", null);
                if (!userData.containsKey("webtoken")) userData.put("webtoken", null);
                if (!userData.containsKey("id")) userData.put("id", null);
                if (!userData.containsKey("userid")) userData.put("userid", null);
                if (!userData.containsKey("lastactivetime")) userData.put("lastactivetime", null);
                return RestApiResponse.buildSuccess(userData, "Session valid");
            } else {
                return RestApiResponse.buildFail("User session not found.");
            }
        } catch (Exception e) {
            LOGGER.error("Exception during session check: " + e.getMessage(), e);
            return RestApiResponse.buildFail("User session not found.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        try {
            // Clear token cookie
            Cookie tokenCookie = new Cookie("token", "");
            tokenCookie.setPath("/");
            tokenCookie.setHttpOnly(false);
            tokenCookie.setMaxAge(0);
            response.addCookie(tokenCookie);

            // Clear session cookie
            Cookie sessionCookie = new Cookie("session", "");
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setMaxAge(0);
            response.addCookie(sessionCookie);

            // Clear refresh token cookie
            Cookie refreshCookie = new Cookie("partner_refreshToken", "");
            refreshCookie.setPath("/");
            refreshCookie.setHttpOnly(false);
            refreshCookie.setMaxAge(0);
            response.addCookie(refreshCookie);

        

            return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully", "User logged out"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Logout failed: " + e.getMessage()));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<SsoApiCallResponse> validateToken(@RequestBody Map<String, String> request) {
        SsoApiCallResponse response = new SsoApiCallResponse();
        SsoApiCallResponse.Result result = new SsoApiCallResponse.Result();
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                response.setStatusCode("400");
                result.setIsActive(false);
                result.setStatus("Token is required");
                response.setResults(List.of(result));
                return ResponseEntity.badRequest().body(response);
            }
            String mobileNumber = jwtService.extractUsername(token);
            boolean isValid = (mobileNumber != null && jwtService.validateToken(token, mobileNumber));
            response.setStatusCode(isValid ? "200" : "401");
            result.setIsActive(isValid);
            if (isValid) {
                // Build user info as in /fetch-user
                result.setStatus("Successfully validated access token - Token Valid.");
                result.setSsoId(mobileNumber);
                result.setUsername("user_" + mobileNumber);
                result.setFirstName("First");
                result.setLastName("Last");
                result.setEmailId("abc@example.com");
                java.util.Map<String, java.util.List<String>> attributes = new java.util.HashMap<>();
                attributes.put("forgot_password_epoch", java.util.List.of("1680000000"));
                attributes.put("mobile", java.util.List.of(mobileNumber));
                attributes.put("hospitalCode", java.util.List.of("HOS-3206"));
                result.setAttributes(attributes);
                result.setClientRoles(java.util.Arrays.asList("ROLE_USER"));
                result.setToken(token);
                // Optionally, get refresh token if available (null if not)
                String refreshToken = null;
                try {
                    SessionData sessionData = sessionService.createSessionData(mobileNumber);
                    if (sessionData != null && sessionData.getWebtoken() != null) {
                        refreshToken = sessionData.getWebtoken().getRefreshToken();
                    }
                } catch (Exception ignored) {}
                result.setRefreshToken(refreshToken);
                result.setTokenExpiresIn(3600);
                result.setRefreshExpiresIn(86400);
                result.setRedirectionUrl(null);
                result.setErrorDesc(null);
                response.setStatusDescription("Success");
                response.setRequestUID(java.util.UUID.randomUUID().toString());
            } else {
                result.setStatus("Token Expired.");
                result.setErrorDesc("Token is invalid or expired");
                response.setStatusDescription("Unauthorized");
                response.setRequestUID(java.util.UUID.randomUUID().toString());
            }
            response.setResults(List.of(result));
            return isValid ? ResponseEntity.ok(response) : ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            response.setStatusCode("500");
            result.setIsActive(false);
            result.setStatus("Token validation failed: " + e.getMessage());
            result.setErrorDesc(e.getMessage());
            response.setResults(List.of(result));
            response.setStatusDescription("Internal Server Error");
            response.setRequestUID(java.util.UUID.randomUUID().toString());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/fetch-user")
    public ResponseEntity<SsoApiCallResponse> fetchUser(@RequestBody Map<String, String> request) {
        String partnerAccessToken = request.get("partnerAccessToken");
        if (partnerAccessToken == null || partnerAccessToken.isEmpty()) {
            partnerAccessToken = request.get("searchValue");
        }
        SsoApiCallResponse response = new SsoApiCallResponse();
        SsoApiCallResponse.Result result = new SsoApiCallResponse.Result();
        try {
            if (partnerAccessToken == null || partnerAccessToken.isEmpty()) {
                response.setStatusCode("400");
                result.setIsActive(false);
                result.setStatus("partnerAccessToken is required");
                response.setResults(java.util.Collections.singletonList(result));
                return ResponseEntity.badRequest().body(response);
            }
            String mobileNumber = jwtService.extractUsername(partnerAccessToken);
            if (mobileNumber == null || mobileNumber.isEmpty()) {
                response.setStatusCode("401");
                result.setIsActive(false);
                result.setStatus("Invalid token");
                response.setResults(java.util.Collections.singletonList(result));
                return ResponseEntity.status(401).body(response);
            }
            // Use hardcoded user info as before
            SessionData sessionData = sessionService.createSessionData(mobileNumber);
            String realRefreshToken = null;
            if (sessionData != null && sessionData.getWebtoken() != null) {
                realRefreshToken = sessionData.getWebtoken().getRefreshToken();
            }
            result.setIsActive(true);
            result.setStatus("Successfully validated access token - Token Valid.");
            result.setSsoId(mobileNumber); // Use mobile as ID
            result.setUsername("user_" + mobileNumber);
            result.setFirstName("First");
            result.setLastName("Last");
            result.setEmailId("abc@example.com");
            java.util.Map<String, java.util.List<String>> attributes = new java.util.HashMap<>();
            attributes.put("forgot_password_epoch", java.util.List.of("1680000000"));
            attributes.put("mobile", java.util.List.of(mobileNumber));
            attributes.put("hospitalCode", java.util.List.of("HOS-3206"));
            result.setAttributes(attributes);
            result.setClientRoles(java.util.Arrays.asList("ROLE_USER"));
            result.setToken(partnerAccessToken);
            result.setRefreshToken(realRefreshToken);
            result.setTokenExpiresIn(3600);
            result.setRefreshExpiresIn(86400);
            result.setRedirectionUrl(null);
            result.setErrorDesc(null);
            response.setStatusCode("200");
            response.setStatusDescription("Success");
            response.setRequestUID(java.util.UUID.randomUUID().toString());
            response.setResults(java.util.Collections.singletonList(result));
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.setStatusCode("500");
            result.setIsActive(false);
            result.setStatus("Internal server error");
            result.setErrorDesc(ex.getMessage());
            response.setResults(java.util.Collections.singletonList(result));
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/generate-token")
    public ResponseEntity<SsoApiCallResponse> generateToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String ssoClientId = request.get("ssoClientId");
        SsoApiCallResponse response = new SsoApiCallResponse();
        SsoApiCallResponse.Result result = new SsoApiCallResponse.Result();

        // Check for missing or malformed refresh token
        if (refreshToken == null || refreshToken.trim().isEmpty() || refreshToken.chars().filter(ch -> ch == '.').count() != 2) {
            response.setStatusCode("400");
            result.setIsActive(false);
            result.setStatus("Refresh token is missing, empty, or not a valid JWT.");
            result.setToken(null);
            response.setResults(List.of(result));
            return ResponseEntity.badRequest().body(response);
        }
        try {
            String mobileNumber = jwtService.extractUsername(refreshToken);
            String newToken = jwtService.refreshToken(refreshToken);
            if (mobileNumber != null && newToken != null) {
                response.setStatusCode("200");
                result.setIsActive(true);
                result.setStatus("Successfully generated access token using refresh token.");
                result.setToken(newToken);
            } else {
                response.setStatusCode("401");
                result.setIsActive(false);
                result.setStatus("Invalid or expired refresh token.");
                result.setToken(null);
            }
            response.setResults(List.of(result));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode("500");
            result.setIsActive(false);
            result.setStatus("Token generation failed: " + e.getMessage());
            result.setToken(null);
            response.setResults(List.of(result));
            return ResponseEntity.status(500).body(response);
        }
    }

}