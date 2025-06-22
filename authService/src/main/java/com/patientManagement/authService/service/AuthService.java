package com.patientManagement.authService.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.patientManagement.authService.dto.LoginRequestDto;
import com.patientManagement.authService.util.JwtUtil;

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

}
