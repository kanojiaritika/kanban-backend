package com.kanban.kanbanProject.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "kanbanprojectsecretkeykanbanprojectsecretkey".getBytes()
    );

    public String generateToken(String emailId) {

        return Jwts.builder()
                .subject(emailId) // subject = who the token belongs to
                .issuedAt(new Date()) // time when token was created
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // current time + {1000ms (1s) + 60sec (1 min) + 60 min (1 hr)}
                .signWith(SECRET_KEY) // creates signature
                .compact(); // converts builder into final JWT String
    }

    public String extractEmail(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token, String emailId) {

        String extractedEmailId = extractEmail(token);

        return extractedEmailId.equals(emailId);
    }
}
