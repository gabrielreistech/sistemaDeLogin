package com.sistema.login.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.io.IOException;
import java.io.PrintWriter;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

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

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws IOException {
        SecurityContextHolder.clearContext();

        jwtFilter = new JwtFilter(jwtUtil, userDetailsService);

        String pathRequest = "pathRequestMock";

        when(request.getRequestURI()).thenReturn(pathRequest);
    }

    @Test
    void pathRequestPublic() throws ServletException, IOException {
        String pathRequestPublic = "/public/";

        when(request.getRequestURI()).thenReturn(pathRequestPublic);

        if (pathRequestPublic.startsWith("/public/")) {
            doNothing().when(filterChain).doFilter(request, response);
        }

        jwtFilter.doFilterInternal(request, response, filterChain);
    }

    @Test
    void testDoFilterInternal_ValidJwtToken() throws ServletException, IOException {
        String validToken = "Bearer valid.jwt.token";
        String username = "testUser";


        when(request.getHeader("Authorization")).thenReturn(validToken);

        when(jwtUtil.extractUsername(anyString())).thenReturn(username);
        when(jwtUtil.validateToken(anyString(), eq(username))).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidAuthHeader() throws ServletException, IOException {
        String invalidAuthHeader = "InvalidHeader";

        when(request.getHeader("Authorization")).thenReturn(invalidAuthHeader);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidJwtToken() throws ServletException, IOException {
        String invalidToken = "Bearer invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn(invalidToken);
        when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("Invalid token"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidUserDetailsAndValidToken() throws ServletException, IOException {
        String authHeader = "Bearer valid_token";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);
        String jwtToken = "valid_token";

        // Mock do UserDetails
        when(userDetails.getUsername()).thenReturn(username);  // Configura o mock para retornar o username

        // Simulação do comportamento
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractUsername(jwtToken)).thenReturn(username);  // Garante que username não seja null
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(jwtToken, username)).thenReturn(true);

        // Executa o método
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Verificações
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Verificação adicional
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be null");
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName(), "Username should match");
    }

}
