package com.teacat.service;

import com.teacat.config.JwtUtil;
import com.teacat.entity.User;
import com.teacat.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtUtil jwtUtil; private final UserRepository users;
    public AuthService(JwtUtil jwtUtil, UserRepository users){ this.jwtUtil=jwtUtil; this.users=users; }
    public User requireUser(String authHeader){
        if(authHeader==null || !authHeader.startsWith("Bearer ")) throw new UnauthorizedException("請先登入");
        try {
            String email=jwtUtil.getEmailFromToken(authHeader.substring(7));
            User user=users.findByEmail(email);
            if(user==null) throw new UnauthorizedException("登入帳號不存在");
            return user;
        } catch (UnauthorizedException e){ throw e; }
        catch (Exception e){ throw new UnauthorizedException("登入狀態已失效"); }
    }
    public static class UnauthorizedException extends RuntimeException { public UnauthorizedException(String m){super(m);} }
}
