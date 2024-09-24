package com.sistema.login.Controllers;

import com.sistema.login.Dtos.ClientDto;
import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Security.JwtUtil;
import com.sistema.login.Services.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private")
public class PrivateController {

    @Autowired
    ClientService clientService;

    @Autowired
    JwtUtil jwtUtil;


    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {

        // Obter o token do cabeçalho Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // Extrair o token
        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            System.out.println("Erro ao extrair o nome de usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }


        // Recuperar os detalhes do usuário
        ClientMeDto user = clientService.findByEmail(username);

        // Retornar as informações do usuário
        return ResponseEntity.ok(user);
    }
    }

