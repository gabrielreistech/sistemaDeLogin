package com.sistema.login.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro para autenticação baseada em JWT (JSON Web Token).
 * Este filtro é responsável por interceptar requisições HTTP, validar o token JWT
 * e definir o contexto de segurança do usuário autenticado.
 */
@SuppressWarnings("ALL")
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Construtor da classe JwtFilter.
     *
     * @param jwtUtil O utilitário JWT utilizado para manipular tokens.
     * @param userDetailsService O serviço que fornece detalhes do usuário.
     */
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Método que realiza o filtro da requisição.
     * Este método verifica se o cabeçalho de autorização é válido,
     * extrai o nome de usuário do token e autentica o usuário se o token for válido.
     *
     * @param request A requisição HTTP recebida.
     * @param response A resposta HTTP a ser enviada.
     * @param filterChain A cadeia de filtros que será chamada após o processamento.
     * @throws ServletException Se ocorrer um erro durante o processamento da requisição.
     * @throws IOException Se ocorrer um erro de entrada/saída durante o processamento.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String pathRequest = request.getRequestURI();

        // Permite que rotas públicas passem sem verificação
        if (pathRequest.startsWith("/public/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (!verificadIfAuthHeaderIsValid(authHeader, response)) {
            return; // Interrompe a execução se o cabeçalho for inválido
        }

        String jwtToken = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Verificação de autenticação
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Se o token for válido
                if (jwtUtil.validateToken(jwtToken, username)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response); // Continua a cadeia de filtros
    }


    /**
     * Verifica se o cabeçalho de autorização é válido.
     *
     * @param authHeader O cabeçalho de autorização a ser verificado.
     * @param response A resposta HTTP para enviar um status se o cabeçalho for inválido.
     * @return true se o cabeçalho for válido, false caso contrário.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    private boolean verificadIfAuthHeaderIsValid(String authHeader, HttpServletResponse response) throws IOException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}


