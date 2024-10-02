package com.sistema.login.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Classe de configuração de segurança para a aplicação.
 * Esta classe define as regras de segurança, incluindo o gerenciamento de autenticação e autorização de requisições HTTP.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Define a cadeia de filtros de segurança para as requisições HTTP.
     *
     * @param http O objeto HttpSecurity que permite a configuração da segurança da aplicação.
     * @return A cadeia de filtros de segurança configurada.
     * @throws Exception Se ocorrer um erro durante a configuração da segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // Permitir acesso ao H2 Console
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin() // Permite o carregamento de frames do mesmo domínio
                        )
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Define um bean para o codificador de senhas.
     *
     * @return Um PasswordEncoder que utiliza o algoritmo BCrypt para codificação de senhas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
