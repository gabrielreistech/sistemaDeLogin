package com.sistema.login.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public JwtUtil(){}

    public JwtUtil(String secretKey) {
        this.secretKey = secretKey;
    }

    public JwtUtil(String secretKey, long expirationTime) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("A chave secreta não pode ser nula ou vazia.");
        }
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return decodeToken(token).getExpiresAt().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}


