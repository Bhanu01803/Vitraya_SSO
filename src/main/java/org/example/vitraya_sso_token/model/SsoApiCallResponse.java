package org.example.vitraya_sso_token.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SsoApiCallResponse {
    private String statusCode;
    private List<Result> results;
    private String statusDescription;
    private String requestUID;

    public static class Result {
        @JsonProperty("isActive")
        private boolean isActive;
        private String status;
        private String ssoId;
        private String username;
        private String firstName;
        private String lastName;
        private String emailId;
        private Object attributes;
        private java.util.List<String> clientRoles;
        private String token;
        private String refreshToken;
        private int tokenExpiresIn;
        private int refreshExpiresIn;
        private String redirectionUrl;
        private String errorDesc;

        @JsonProperty("isActive")
        public boolean getIsActive() { return isActive; }
        public void setIsActive(boolean isActive) { this.isActive = isActive; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getSsoId() { return ssoId; }
        public void setSsoId(String ssoId) { this.ssoId = ssoId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmailId() { return emailId; }
        public void setEmailId(String emailId) { this.emailId = emailId; }
        public Object getAttributes() { return attributes; }
        public void setAttributes(Object attributes) { this.attributes = attributes; }
        public java.util.List<String> getClientRoles() { return clientRoles; }
        public void setClientRoles(java.util.List<String> clientRoles) { this.clientRoles = clientRoles; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public int getTokenExpiresIn() { return tokenExpiresIn; }
        public void setTokenExpiresIn(int tokenExpiresIn) { this.tokenExpiresIn = tokenExpiresIn; }
        public int getRefreshExpiresIn() { return refreshExpiresIn; }
        public void setRefreshExpiresIn(int refreshExpiresIn) { this.refreshExpiresIn = refreshExpiresIn; }
        public String getRedirectionUrl() { return redirectionUrl; }
        public void setRedirectionUrl(String redirectionUrl) { this.redirectionUrl = redirectionUrl; }
        public String getErrorDesc() { return errorDesc; }
        public void setErrorDesc(String errorDesc) { this.errorDesc = errorDesc; }
    }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    public List<Result> getResults() { return results; }
    public void setResults(List<Result> results) { this.results = results; }
    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }
    public String getRequestUID() { return requestUID; }
    public void setRequestUID(String requestUID) { this.requestUID = requestUID; }
} 