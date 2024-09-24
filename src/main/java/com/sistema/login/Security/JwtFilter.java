package com.sistema.login.Security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Definir status 401 se o cabeçalho de autorização estiver ausente ou inválido
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }

        // Verifica se o cabeçalho Authorization está presente e começa com "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7); // Remove "Bearer " do token
            try {
                username = jwtUtil.extractUsername(jwtToken); // Extrai o nome de usuário do token
            } catch (RuntimeException e) {
                // Retorna resposta 401 se o token for inválido
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized");
                return; // Interrompe o processamento se o token for inválido
            }
        }

        // Se um nome de usuário foi extraído e não há uma autenticação atual no contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Valida o token e configura a autenticação no contexto de segurança
            if (jwtUtil.validateToken(jwtToken, username)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // Continua a cadeia de filtros
    }
}


