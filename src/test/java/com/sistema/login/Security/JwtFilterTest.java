package com.sistema.login.Security;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void testDoFilterInternal_WithValidToken() throws IOException, ServletException {
        String token = "Bearer validToken";
        String expectedUsername = "test@example.com";
        UserDetails userDetails = mock(UserDetails.class);

        lenient().when(request.getHeader("Authorization")).thenReturn(token);
        lenient().when(jwtUtil.extractUsername("validToken")).thenReturn(expectedUsername);
        lenient().when(jwtUtil.validateToken("validToken", expectedUsername)).thenReturn(true);
        lenient().when(userDetailsService.loadUserByUsername(expectedUsername)).thenReturn(userDetails);
        lenient().when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication, "Authentication should be set");
        assertEquals(expectedUsername, authentication.getName(), "Username should match");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        String token = "Bearer invalidToken";
        PrintWriter mockWriter = mock(PrintWriter.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("invalidToken")).thenThrow(new RuntimeException("Invalid token"));
        when(response.getWriter()).thenReturn(mockWriter);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockWriter).write("Unauthorized"); // Verifica se o método write foi chamado
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AuthHeaderNull() throws IOException, ServletException {
        PrintWriter mockWriter = mock(PrintWriter.class);

        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(mockWriter);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testDoFilterInternal_ValidToken_ShouldSetAuthentication() throws IOException, ServletException {
        String token = "Bearer validToken";
        String username = "test@example.com";
        UserDetails userDetails = mock(UserDetails.class);

        // Configure o mock de UserDetails para retornar o username esperado
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("validToken")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken("validToken", username)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        // Verifique se a autenticação foi definida no contexto de segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(username, authentication.getName()); // Verifica se o nome do usuário corresponde
    }

    @Test
    void testDoFilterInternal_WithoutAuthorizationHeader() throws IOException, ServletException {
        PrintWriter mockWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(mockWriter); // Certifique-se de que isso está configurado

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Verifica se o status foi definido
    }
}
