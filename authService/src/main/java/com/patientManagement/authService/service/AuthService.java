package com.patientManagement.authService.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.patientManagement.authService.dto.LoginRequestDto;
import com.patientManagement.authService.util.JwtUtil;

import io.jsonwebtoken.JwtException;

@Service
public class AuthService {
    
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Optional<String> authenticate(LoginRequestDto loginRequestDto){
        Optional<String> token = userService.findByEmail(loginRequestDto.getEmail())
        .filter(user -> passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword()))
        .map(user -> jwtUtil.generateToken(user.getEmail(),user.getPassword()));

        return token;
    }

    public boolean validateToken(String token){
        try {
            jwtUtil.validateToken(token);
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }

}
