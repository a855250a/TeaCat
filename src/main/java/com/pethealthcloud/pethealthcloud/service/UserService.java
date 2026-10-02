package com.pethealthcloud.pethealthcloud.service;

import com.pethealthcloud.pethealthcloud.config.JwtUtil;
import com.pethealthcloud.pethealthcloud.dto.LoginRequest;
import com.pethealthcloud.pethealthcloud.dto.LoginResponse;
import com.pethealthcloud.pethealthcloud.dto.RegisterRequest;
import com.pethealthcloud.pethealthcloud.dto.RegisterResponse;
import com.pethealthcloud.pethealthcloud.entity.User;
import com.pethealthcloud.pethealthcloud.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// 使用者相關商業邏輯
@Service
public class UserService {

        private final UserRepository userRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

        // Demo 訪客帳號
        private static final String GUEST_EMAIL = "guest@teacat.local";
        private static final String GUEST_PASSWORD = "TeaCatGuest";

        public UserService(
                        UserRepository userRepository,
                        BCryptPasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtil = jwtUtil;
        }

        // =========================
        // 一般登入
        // =========================
        public LoginResponse login(LoginRequest loginRequest) {

                User user = userRepository.findByEmail(
                                loginRequest.getEmail());

                LoginResponse response = new LoginResponse();

                if (user == null) {
                        response.setMessage("帳號不存在");
                        return response;
                }

                if (!passwordEncoder.matches(
                                loginRequest.getPassword(),
                                user.getPassword())) {
                        response.setMessage("密碼錯誤");
                        return response;
                }

                return createLoginResponse(user);
        }

        // =========================
        // 註冊
        // =========================
        public RegisterResponse register(
                        RegisterRequest registerRequest) {

                User existUser = userRepository.findByEmail(
                                registerRequest.getEmail());

                RegisterResponse response = new RegisterResponse();

                if (existUser != null) {
                        response.setMessage("Email已存在");
                        return response;
                }

                User user = new User();

                user.setEmail(registerRequest.getEmail());

                user.setPassword(
                                passwordEncoder.encode(
                                                registerRequest.getPassword()));

                userRepository.save(user);

                response.setMessage("註冊成功");

                return response;
        }

        // =========================
        // 訪客一鍵登入
        // =========================
        public LoginResponse guestLogin() {

                User guest = userRepository.findByEmail(
                                GUEST_EMAIL);

                // 第一次使用時自動建立 Demo 帳號
                if (guest == null) {

                        guest = new User();

                        guest.setEmail(GUEST_EMAIL);

                        guest.setPassword(
                                        passwordEncoder.encode(
                                                        GUEST_PASSWORD));

                        guest = userRepository.save(guest);
                }

                // 每次訪客登入都產生新的 JWT
                return createLoginResponse(guest);
        }

        // =========================
        // 建立登入成功 Response
        // =========================
        private LoginResponse createLoginResponse(User user) {

                LoginResponse response = new LoginResponse();

                String token = jwtUtil.generateToken(
                                user.getEmail());

                response.setId(user.getId());
                response.setEmail(user.getEmail());
                response.setToken(token);
                response.setMessage("登入成功");

                return response;
        }
}