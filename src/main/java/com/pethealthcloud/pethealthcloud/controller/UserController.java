package com.pethealthcloud.pethealthcloud.controller;

import com.pethealthcloud.pethealthcloud.dto.LoginRequest;
import com.pethealthcloud.pethealthcloud.dto.LoginResponse;
import com.pethealthcloud.pethealthcloud.dto.RegisterRequest;
import com.pethealthcloud.pethealthcloud.dto.RegisterResponse;
import com.pethealthcloud.pethealthcloud.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

        private final UserService userService;

        public UserController(
                        UserService userService) {
                this.userService = userService;
        }

        // =========================
        // 一般登入
        // =========================
        @PostMapping("/login")
        public LoginResponse login(
                        @RequestBody LoginRequest loginRequest) {
                return userService.login(loginRequest);
        }

        // =========================
        // 註冊
        // =========================
        @PostMapping("/register")
        public RegisterResponse register(
                        @RequestBody RegisterRequest registerRequest) {
                return userService.register(registerRequest);
        }

        // =========================
        // 訪客一鍵登入
        // =========================
        @PostMapping("/guest-login")
        public LoginResponse guestLogin() {
                return userService.guestLogin();
        }
}