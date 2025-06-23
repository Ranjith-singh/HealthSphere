package com.patientManagement.authService.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {
    private final Key secretKey;

    Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(@Value("${jwt.secret}") String secret){
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role){
        return Jwts.builder()
        .subject(email)
        .claim("role",role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(secretKey)
        .compact();
    }

    public void validateToken(String token){
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)
            .build()
            .parseSignedClaims(token);
        }
        catch(SignatureException e){
            logger.warn("Invalid Jwt Signature, Credentials leaked!");
            throw new JwtException("Invalid Jwt Signature");
        }
        catch(JwtException e){
            throw new JwtException("Invalid Jwt");
        }
    }
}
