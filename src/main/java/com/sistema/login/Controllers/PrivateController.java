package com.sistema.login.Controllers;

import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Security.JwtUtil;
import com.sistema.login.Services.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controlador privado responsável por fornecer informações detalhadas do cliente autenticado.
 * As rotas expostas por este controlador requerem autenticação via JWT.
 */
@RestController
@RequestMapping("/private")
public class PrivateController {

    private final ClientService clientService;

    private final JwtUtil jwtUtil;

    /**
     * Construtor do {@link PrivateController}.
     *
     * @param jwtUtil       Utilitário para manipulação de JWT.
     * @param clientService Serviço responsável pelas operações de cliente.
     */
    @Autowired
    public PrivateController(JwtUtil jwtUtil, ClientService clientService) {
        this.jwtUtil = jwtUtil;
        this.clientService = clientService;
    }

    /**
     * Endpoint para obter detalhes do cliente autenticado.
     * Requer um token JWT válido no cabeçalho da requisição para autenticação.
     *
     * @param request {@link HttpServletRequest} contendo informações da requisição HTTP, incluindo o token JWT.
     * @return {@link ResponseEntity} contendo um {@link ClientMeDto} com os detalhes do cliente autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        try {
            String username = this.clientService.getUserDetailsService(request);
            ClientMeDto user = clientService.findByEmail(username);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

