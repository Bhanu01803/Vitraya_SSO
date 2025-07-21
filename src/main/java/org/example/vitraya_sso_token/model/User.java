package org.example.vitraya_sso_token.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String mobileNumber;
    private String email;
    private String blocked;
    private String userid;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean isNewUser;
    private String lastLoginType;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBlocked() { return blocked; }
    public void setBlocked(String blocked) { this.blocked = blocked; }
    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public boolean isNewUser() { return isNewUser; }
    public void setNewUser(boolean newUser) { isNewUser = newUser; }
    public String getLastLoginType() { return lastLoginType; }
    public void setLastLoginType(String lastLoginType) { this.lastLoginType = lastLoginType; }
} 