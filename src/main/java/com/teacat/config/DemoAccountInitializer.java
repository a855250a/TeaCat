package com.teacat.config;

import com.teacat.entity.User;
import com.teacat.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoAccountInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;

    public DemoAccountInitializer(UserRepository users, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        // Portfolio owner/test account: username a / password a.
        // Keep this account deterministic on every startup so an older cloud DB
        // cannot leave the portfolio owner locked out with a stale password hash.
        User user = users.findByEmail("a");
        if (user == null) {
            user = new User();
            user.setEmail("a");
        }
        user.setPassword(encoder.encode("a"));
        users.save(user);
    }
}
