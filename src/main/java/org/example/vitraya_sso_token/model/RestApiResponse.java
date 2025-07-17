package org.example.vitraya_sso_token.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private String errorCode;

    public RestApiResponse() {}

    public RestApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public RestApiResponse(boolean success, String message, Object data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static RestApiResponse buildSuccess(Object data) {
        return new RestApiResponse(true, "Success", data);
    }

    public static RestApiResponse buildSuccess(Object data, String message) {
        return new RestApiResponse(true, message, data);
    }

    public static RestApiResponse buildFail(String message) {
        return new RestApiResponse(false, message, null);
    }

    public static RestApiResponse buildFail(String message, String errorCode) {
        return new RestApiResponse(false, message, null, errorCode);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
} 