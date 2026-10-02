package com.teacat.dto;
// 使用者註冊請求資料
public class RegisterRequest {
    // 使用者信箱
    private String email;
    // 使用者密碼
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}