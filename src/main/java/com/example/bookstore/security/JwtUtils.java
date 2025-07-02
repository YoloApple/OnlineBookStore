package com.example.bookstore.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${bookstore.jwtSecret}")
    private String jwtSecret;

    @Value("${bookstore.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Tạo key từ chuỗi bí mật base64
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())  // Changed from setSubject
                .issuedAt(new Date())                  // Changed from setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))  // Changed from setExpiration
                .signWith(getSigningKey())             // Removed SignatureAlgorithm parameter
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()                          // Changed from parserBuilder
                .verifyWith(getSigningKey())          // Changed from setSigningKey
                .build()
                .parseSignedClaims(token)             // Changed from parseClaimsJws
                .getPayload()                         // Changed from getBody
                .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()                             // Changed from parserBuilder
                    .verifyWith(getSigningKey())          // Changed from setSigningKey
                    .build()
                    .parseSignedClaims(token);            // Changed from parseClaimsJws
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Log chi tiết lỗi nếu cần
            return false;
        }
    }
}