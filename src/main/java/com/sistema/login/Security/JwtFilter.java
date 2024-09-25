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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@SuppressWarnings("ALL")
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private static String username;

    private static String jwtToken;


    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String pathRequest = request.getRequestURI();

        if(pathRequest.startsWith("/public/")){
            filterChain.doFilter(request, response);
        }

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if(!verificadIfAuthHeaderIsValid(authHeader, response) ||!verificadAuthHeaderIsPresentAndFirstWithBearer(authHeader, response, jwtUtil)
                || !userNameNotNullAndSecurityContextHolderNullAndValidateToken(userDetailsService, jwtUtil)){
            return;
        }

        filterChain.doFilter(request, response); // Continua a cadeia de filtros
    }

    public static boolean verificadIfAuthHeaderIsValid(String authHeader, HttpServletResponse response) throws IOException {
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    public static boolean verificadAuthHeaderIsPresentAndFirstWithBearer(String authHeader, HttpServletResponse response, JwtUtil jwtUtil) throws IOException {
            jwtToken = authHeader.substring(7);
            try {
                 username = jwtUtil.extractUsername(jwtToken);
            }catch (RuntimeException e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        return true;
    }

    public static boolean userNameNotNullAndSecurityContextHolderNullAndValidateToken(UserDetailsService userDetailsService, JwtUtil jwtUtil){
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(userDetails != null && jwtUtil.validateToken(jwtToken, username)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                return true;
            }
        }
        return false;
    }
}


