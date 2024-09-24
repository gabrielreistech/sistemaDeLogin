package com.sistema.login.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil("chave_secreta");
    }

    @Test
    void generateToken() {
        String secretMock = "secretMock";
        String userNameMock = "UserNameMock";
        String tokenMock = "123456789";
        long expirationTime = 3600000; // 1 hora

        // Mock do algoritmo
        try (MockedStatic<Algorithm> mockedAlgorithm = mockStatic(Algorithm.class);
             MockedStatic<JWT> mockedJwt = mockStatic(JWT.class)) {

            // Mock do algoritmo HMAC256
            Algorithm algorithmMock = mock(Algorithm.class);
            mockedAlgorithm.when(() -> Algorithm.HMAC256(secretMock)).thenReturn(algorithmMock);

            // Mock do JWT Creator
            JWTCreator.Builder jwtBuilderMock = mock(JWTCreator.Builder.class);
            mockedJwt.when(JWT::create).thenReturn(jwtBuilderMock);

            // Configurar o comportamento do JWT Builder
            when(jwtBuilderMock.withSubject(userNameMock)).thenReturn(jwtBuilderMock);
            when(jwtBuilderMock.withIssuedAt(any(Date.class))).thenReturn(jwtBuilderMock);
            when(jwtBuilderMock.withExpiresAt(any(Date.class))).thenReturn(jwtBuilderMock);
            when(jwtBuilderMock.sign(algorithmMock)).thenReturn(tokenMock);

            // Instanciar JwtUtil com o secretMock e expirationTime
            JwtUtil jwtUtil = new JwtUtil(secretMock, expirationTime);

            // Chamar o método
            String token = jwtUtil.generateToken(userNameMock);

            // Verificar o resultado
            assertEquals(tokenMock, token);
        }
    }

    @Test
    void generateTokenThrowsException() {
        String secretMock = "secretMock";
        String userNameMock = "UserNameMock";
        long expirationTime = 3600000; // 1 hora

        // Mock do algoritmo
        try (MockedStatic<Algorithm> mockedAlgorithm = mockStatic(Algorithm.class);
             MockedStatic<JWT> mockedJwt = mockStatic(JWT.class)) {

            // Mock do algoritmo HMAC256
            Algorithm algorithmMock = mock(Algorithm.class);
            mockedAlgorithm.when(() -> Algorithm.HMAC256(secretMock)).thenReturn(algorithmMock);

            // Mock do JWT Creator
            JWTCreator.Builder jwtBuilderMock = mock(JWTCreator.Builder.class);
            mockedJwt.when(JWT::create).thenReturn(jwtBuilderMock);

            // Configurar o comportamento do JWT Builder para lançar uma exceção
            when(jwtBuilderMock.withSubject(userNameMock)).thenReturn(jwtBuilderMock);
            when(jwtBuilderMock.withIssuedAt(any(Date.class))).thenReturn(jwtBuilderMock);
            when(jwtBuilderMock.withExpiresAt(any(Date.class))).thenReturn(jwtBuilderMock);

            // Simular uma exceção ao chamar o método sign
            when(jwtBuilderMock.sign(algorithmMock)).thenThrow(new RuntimeException("Token signing failed"));

            // Instanciar JwtUtil com o secretMock e expirationTime
            JwtUtil jwtUtil = new JwtUtil(secretMock, expirationTime);

            // Verificar se a exceção é lançada
            Exception exception = assertThrows(RuntimeException.class, () -> {
                jwtUtil.generateToken(userNameMock);
            });

            // Verificar a mensagem da exceção
            assertEquals("Token signing failed", exception.getMessage());
        }
    }

    @Test
    public void decodeTokenSuccess() {
        // Arrange
        String validToken = "seu_token_valido";
        String secretKey = "sua_chave_secreta";

        // Mocks
        Algorithm mockAlgorithm = mock(Algorithm.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);
        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);

        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(validToken)).thenReturn(mockDecodedJWT);

            // Act
            DecodedJWT result = jwtUtil.decodeToken(validToken);

            // Assert
            assertNotNull(result);
        }
    }

    @Test
    public void decodeTokenThrowsException() {
        // Arrange
        String invalidToken = "token_invalido";

        // Mocks
        Algorithm mockAlgorithm = mock(Algorithm.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);

        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(invalidToken)).thenThrow(new JWTVerificationException("Token inválido"));

            // Act & Assert
            assertThrows(JWTVerificationException.class, () -> {
                jwtUtil.decodeToken(invalidToken);
            });
        }
    }

    @Test
    void decodeException(){
        String validToken = "valid_token";

        jwtUtil = new JwtUtil(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> jwtUtil.decodeToken(validToken));
    }

    @Test
    void decodeEmpty(){
        String validToken = "valid_token";

        jwtUtil = new JwtUtil("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> jwtUtil.decodeToken(validToken));
    }


    @Test
    public void extractUsernameSuccess() {
        // Arrange
        String validToken = "seu_token_valido";
        String expectedUsername = "usuario@example.com"; // Exemplo de nome de usuário esperado
        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        Verification mockVerification = mock(Verification.class);
        Algorithm mockAlgorithm = mock(Algorithm.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);

        // Mock static JWT
        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(validToken)).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getSubject()).thenReturn(expectedUsername);

            // Act
            String result = jwtUtil.extractUsername(validToken);

            // Assert
            assertEquals(expectedUsername, result);
        }
    }

    @Test
    public void extractUsernameThrowsException() {
        // Arrange
        String invalidToken = "token_invalido";
        Algorithm mockAlgorithm = mock(Algorithm.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);

        // Mock static JWT
        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento para que JWT.require() retorne um mockVerification
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(invalidToken)).thenThrow(new JWTVerificationException("Token inválido"));

            // Act & Assert
            assertThrows(JWTVerificationException.class, () -> {
                jwtUtil.extractUsername(invalidToken);
            });
        }
    }

    @Test
    public void isTokenExpiredReturnsTrue() {
        // Arrange
        String expiredToken = "token_expirado";
        Date expiredDate = new Date(System.currentTimeMillis() - 1000); // Data no passado

        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);

        // Mock static JWT
        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(expiredToken)).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getExpiresAt()).thenReturn(expiredDate);

            // Act
            boolean result = jwtUtil.isTokenExpired(expiredToken);

            // Assert
            assertTrue(result);
        }
    }

    @Test
    public void isTokenExpiredReturnsFalse() {
        // Arrange
        String validToken = "token_valido";
        Date futureDate = new Date(System.currentTimeMillis() + 1000); // Data no futuro

        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);

        // Mock static JWT
        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(validToken)).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getExpiresAt()).thenReturn(futureDate);

            // Act
            boolean result = jwtUtil.isTokenExpired(validToken);

            // Assert
            assertFalse(result);
        }
    }

    @Test
    public void validateTokenReturnsTrue() {
        // Arrange
        String validToken = "token_valido";
        String username = "usuario_teste";

        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);

        // Mock static JWT
        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(validToken)).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getSubject()).thenReturn(username);
            when(mockDecodedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token não expirado

            // Act
            boolean result = jwtUtil.validateToken(validToken, username);

            // Assert
            assertTrue(result);
        }
    }

    @Test
    public void validateTokenReturnsFalseForExpiredToken() {
        // Arrange
        String validToken = "token_expirado";
        String username = "usuario_teste";

        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);

        // Mock static JWT
        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {
            // Simulando o comportamento
            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(validToken)).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getSubject()).thenReturn(username);
            when(mockDecodedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() - 10000)); // Token expirado

            // Act
            boolean result = jwtUtil.validateToken(validToken, username);

            // Assert
            assertFalse(result);
        }
    }

    @Test
    public void validateTokenReturnsFalseForDifferentUsername() {

        String validToken = "token_valido";
        String username = "usuario_teste";
        String differentUsername = "usuario_diferente";

        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        Verification mockVerification = mock(Verification.class);
        JWTVerifier mockVerifier = mock(JWTVerifier.class);


        try (MockedStatic<JWT> mockedJWT = mockStatic(JWT.class)) {

            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(mockVerification);
            when(mockVerification.build()).thenReturn(mockVerifier);
            when(mockVerifier.verify(validToken)).thenReturn(mockDecodedJWT);

            when(mockDecodedJWT.getSubject()).thenReturn(username);

            when(jwtUtil.decodeToken(validToken)).thenReturn(mockDecodedJWT);

            boolean result = jwtUtil.validateToken(validToken, differentUsername);

            assertFalse(result);
        }
    }
}