package com.sistema.login.Controllers;

import com.sistema.login.Dtos.ClientDto;
import com.sistema.login.Dtos.ClientLoginDto;
import com.sistema.login.Services.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador público responsável por gerenciar o cadastro (signup) e login (signin) de clientes.
 * As rotas expostas por esse controlador são de acesso público e não requerem autenticação prévia.
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    /**
     * Serviço responsável pelas operações relacionadas aos clientes.
     */
    @Autowired
    ClientService clientService;

    /**
     * Endpoint para cadastro de um novo cliente.
     * Recebe um DTO contendo as informações do cliente e retorna um token de autenticação.
     *
     * @param clientDto DTO contendo as informações do cliente que será cadastrado.
     * @return ResponseEntity com o token JWT gerado para o cliente.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> create(@Valid @RequestBody ClientDto clientDto) {
        String tokenClient = this.clientService.create(clientDto);
        return ResponseEntity.ok(tokenClient);
    }

    /**
     * Endpoint para autenticação de um cliente existente.
     * Recebe um DTO com as credenciais de login (email e senha) e retorna um token de autenticação.
     *
     * @param clientLoginDto DTO contendo as credenciais de login do cliente.
     * @return ResponseEntity com o token JWT gerado para o cliente autenticado.
     */
    @PostMapping("/signin")
    public ResponseEntity<String> login(@Valid @RequestBody ClientLoginDto clientLoginDto) {
        String clientLogin = this.clientService.login(clientLoginDto);
        return ResponseEntity.ok(clientLogin);
    }
}