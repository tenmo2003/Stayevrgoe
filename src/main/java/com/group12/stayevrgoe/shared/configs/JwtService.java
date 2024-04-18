package com.group12.stayevrgoe.shared.configs;

import com.group12.stayevrgoe.shared.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtService {

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(JwtConstants.SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public String generateToken(String email) {
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + JwtConstants.JWT_TOKEN_VALIDITY);

        return Jwts.builder()
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, JwtConstants.SECRET_KEY)
                .compact();
    }

    public boolean isTokenValid(String token) {
        Date currentDate = new Date();
        return currentDate.before(extractClaims(token).getExpiration());
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
}
