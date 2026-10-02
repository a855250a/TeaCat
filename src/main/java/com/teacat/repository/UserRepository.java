package com.teacat.repository;

import com.teacat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {
        // 根據 Email 查詢使用者
        User findByEmail(String email);
    }

