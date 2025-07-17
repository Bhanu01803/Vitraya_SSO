package org.example.vitraya_sso_token.model;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class SessionData {
    private Map<String, Object> data;
    private Corporate corporate;  // Added this field
    private WebToken webtoken;
    private String id;
    private String userid;
    private Date lastactivetime;

    public static class WebToken {
        private String token;
        private String refreshToken;
        private String expiryat;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public String getExpiryat() { return expiryat; }
        public void setExpiryat(String expiryat) { this.expiryat = expiryat; }
    }
}