package org.example.vitraya_sso_token.model;

import jakarta.validation.constraints.NotBlank;

public class ValidateTokenRequest {
    @NotBlank(message = "Token is required")
    private String token;

    private String mobileNumber;

    public ValidateTokenRequest() {}

    public ValidateTokenRequest(String token, String mobileNumber) {
        this.token = token;
        this.mobileNumber = mobileNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
} 