package com.desafio.pitang.Controllers;

import com.desafio.pitang.Dtos.ClientDto;
import com.desafio.pitang.Dtos.ClientMeDto;
import com.desafio.pitang.Security.JwtUtil;
import com.desafio.pitang.Services.ClientService;
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

    @GetMapping("/clients")
    public ResponseEntity<Page<ClientDto>> findAll(
           @RequestParam(defaultValue = "0") @Min(0) int page,
           @RequestParam(defaultValue = "10") @Min(1) int size,
           @RequestParam(defaultValue = "firstName") String sort
    ){
       Page<ClientDto> pageResponse = this.clientService.findAll(page, size, sort);
          return ResponseEntity.ok(pageResponse);
    }

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - invalid session");
        }

        // Recuperar os detalhes do usuário
        ClientMeDto user = clientService.findByEmail(username);

        // Retornar as informações do usuário
        return ResponseEntity.ok(user);
    }
    }

