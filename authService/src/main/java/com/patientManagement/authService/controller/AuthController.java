package com.patientManagement.authService.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.patientManagement.authService.dto.LoginRequestDto;
import com.patientManagement.authService.dto.LoginResponseDto;
import com.patientManagement.authService.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name="AuthService", description = "api's of auth service")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Generate tokens on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginResponseDto){
        Optional<String> optionalToken = authService.authenticate(loginResponseDto);
        if(optionalToken.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token=optionalToken.get();
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}

