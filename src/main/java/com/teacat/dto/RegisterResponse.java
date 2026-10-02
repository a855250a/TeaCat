package com.teacat.dto;

// 使用者註冊成功後回傳給前端的資料
public class RegisterResponse {
    // 註冊結果訊息
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}