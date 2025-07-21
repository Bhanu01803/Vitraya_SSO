package org.example.vitraya_sso_token.model;

import jakarta.persistence.*;

@Entity
@Table(name = "webtokens")
public class WebToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private String refreshToken;
    private String expiryAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getExpiryAt() { return expiryAt; }
    public void setExpiryAt(String expiryAt) { this.expiryAt = expiryAt; }
}