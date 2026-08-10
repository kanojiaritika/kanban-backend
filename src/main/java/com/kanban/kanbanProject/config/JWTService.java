package com.kanban.kanbanProject.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JWTService {

    private static final String secretKey = "my_secret_key_harry_potter_and_the_chamber_of_secrets_of_kanban_applications";

    private SecretKey signKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String emailId, String firstName, String lastName) {
        return Jwts.builder()
                .subject(emailId)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(signKey())
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
