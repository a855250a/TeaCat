package com.teacat.service;

import com.teacat.config.JwtUtil;
import com.teacat.dto.LoginRequest;
import com.teacat.dto.LoginResponse;
import com.teacat.dto.RegisterRequest;
import com.teacat.dto.RegisterResponse;
import com.teacat.entity.User;
import com.teacat.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.teacat.repository.PetRepository;
import com.teacat.repository.HealthRecordRepository;
import com.teacat.entity.Pet;
import com.teacat.entity.HealthRecord;
import java.time.LocalDate;

// 使用者相關商業邏輯
@Service
public class UserService {

        private final UserRepository userRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final PetRepository petRepository;
        private final HealthRecordRepository healthRecordRepository;

        // Demo 訪客帳號
        private static final String GUEST_EMAIL = "guest@teacat.local";
        private static final String GUEST_PASSWORD = "TeaCatGuest";

        public UserService(
                        UserRepository userRepository,
                        BCryptPasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil,
                        PetRepository petRepository,
                        HealthRecordRepository healthRecordRepository) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtil = jwtUtil;
                this.petRepository = petRepository;
                this.healthRecordRepository = healthRecordRepository;
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
        @Transactional
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

                // 每次訪客登入都重建固定 Demo 資料，避免前一位訪客的修改/刪除
                // 影響下一位作品集瀏覽者。
                resetGuestData(guest);

                // 每次訪客登入都產生新的 JWT
                return createLoginResponse(guest);
        }


        private void resetGuestData(User guest) {
                healthRecordRepository.deleteByUserId(guest.getId());
                petRepository.deleteByUserId(guest.getId());

                Pet pet = new Pet();
                pet.name = "茶茶";
                pet.age = 3;
                pet.weight = 4.3;
                pet.vaccine = "三合一疫苗";
                pet.setUser(guest);
                pet = petRepository.save(pet);

                HealthRecord r1 = new HealthRecord();
                r1.setPet(pet); r1.setUser(guest);
                r1.setRecordDate(LocalDate.now().minusDays(20));
                r1.setType("VACCINE"); r1.setTitle("年度疫苗");
                r1.setNotes("完成例行疫苗紀錄");
                healthRecordRepository.save(r1);

                HealthRecord r2 = new HealthRecord();
                r2.setPet(pet); r2.setUser(guest);
                r2.setRecordDate(LocalDate.now().minusDays(7));
                r2.setType("WEIGHT"); r2.setTitle("體重紀錄");
                r2.setWeight(4.3); r2.setNotes("精神與食慾正常");
                healthRecordRepository.save(r2);
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