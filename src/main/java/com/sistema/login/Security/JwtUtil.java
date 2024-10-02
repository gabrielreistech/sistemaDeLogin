package com.sistema.login.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Classe utilitária para manipulação de JSON Web Tokens (JWT).
 * Esta classe fornece métodos para gerar, decodificar e validar tokens JWT.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Construtor padrão.
     */
    public JwtUtil() {}

    /**
     * Construtor com chave secreta.
     *
     * @param secretKey A chave secreta utilizada para assinar o token.
     */
    public JwtUtil(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Construtor com chave secreta e tempo de expiração.
     *
     * @param secretKey A chave secreta utilizada para assinar o token.
     * @param expirationTime O tempo de expiração do token em milissegundos.
     */
    public JwtUtil(String secretKey, long expirationTime) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    /**
     * Gera um token JWT para um usuário específico.
     *
     * @param username O nome do usuário para o qual o token será gerado.
     * @return O token JWT gerado.
     */
    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    /**
     * Decodifica um token JWT.
     *
     * @param token O token JWT a ser decodificado.
     * @return Um objeto DecodedJWT contendo as informações decodificadas do token.
     * @throws IllegalArgumentException se a chave secreta for nula ou vazia.
     */
    public DecodedJWT decodeToken(String token) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("A chave secreta não pode ser nula ou vazia.");
        }
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    /**
     * Extrai o nome do usuário de um token JWT.
     *
     * @param token O token JWT do qual o nome do usuário será extraído.
     * @return O nome do usuário contido no token.
     */
    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }

    /**
     * Verifica se um token JWT está expirado.
     *
     * @param token O token JWT a ser verificado.
     * @return true se o token estiver expirado, false caso contrário.
     */
    public boolean isTokenExpired(String token) {
        return decodeToken(token).getExpiresAt().before(new Date());
    }

    /**
     * Valida um token JWT com base no nome de usuário.
     *
     * @param token O token JWT a ser validado.
     * @param username O nome do usuário a ser verificado.
     * @return true se o token for válido e pertencer ao usuário, false caso contrário.
     */
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}

