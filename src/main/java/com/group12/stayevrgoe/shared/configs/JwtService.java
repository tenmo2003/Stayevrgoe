package com.group12.stayevrgoe.shared.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.SystemProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
import java.util.Date;

@Component
@Slf4j
public class JwtService {
    private SecretKey secretKey;
    private static final long JWT_TOKEN_TIME_TO_LIVE = (long) 1000 * 60 * 60 * 24 * 7;

    @PostConstruct
    private void init() {
        try {
            String secretKeyFilePathname = SystemProperties.get("user.dir")
                    + File.separator
                    + "certs"
                    + File.separator
                    + "secret.pem";
            File keyFile = new File(secretKeyFilePathname);
            byte[] bytes = Files.readAllBytes(keyFile.toPath());
            secretKey = Keys.hmacShaKeyFor(bytes);
        } catch (Exception e) {
            log.error("Error initializing JWT service: {}", e.getMessage());
        }
    }


    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(String email) {
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + JWT_TOKEN_TIME_TO_LIVE);

        return Jwts.builder()
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
}
