package org.example.vitraya_sso_token.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Corporate corporate;

    @OneToOne(cascade = CascadeType.ALL)
    private WebToken webToken;

    private Date lastActiveTime;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Corporate getCorporate() { return corporate; }
    public void setCorporate(Corporate corporate) { this.corporate = corporate; }
    public WebToken getWebToken() { return webToken; }
    public void setWebToken(WebToken webToken) { this.webToken = webToken; }
    public Date getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(Date lastActiveTime) { this.lastActiveTime = lastActiveTime; }
} 