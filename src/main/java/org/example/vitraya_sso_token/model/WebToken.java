package org.example.vitraya_sso_token.model;

import lombok.Data;

@Data
public class WebToken {
    private String token;
    private String refreshToken;
    private String expiryat;

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getExpiryat() { return expiryat; }
    public void setExpiryat(String expiryat) { this.expiryat = expiryat; }
}