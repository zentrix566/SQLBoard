package com.sqlboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
public class LoginResponse {

    private String token;
    private String username;
    private String nickname;
    private Boolean success;
    private String message;

    public static LoginResponse success(String token, String username, String nickname) {
        LoginResponse resp = new LoginResponse();
        resp.setSuccess(true);
        resp.setToken(token);
        resp.setUsername(username);
        resp.setNickname(nickname);
        return resp;
    }

    public static LoginResponse fail(String message) {
        LoginResponse resp = new LoginResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
