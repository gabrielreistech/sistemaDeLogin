package com.sistema.login.Controllers;

import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Security.JwtUtil;
import com.sistema.login.Services.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private")
public class PrivateController {

    private final ClientService clientService;

    private final JwtUtil jwtUtil;

    @Autowired
    public PrivateController(JwtUtil jwtUtil, ClientService clientService) {
        this.jwtUtil = jwtUtil;
        this.clientService = clientService;
    }


    @GetMapping("/me")
    public ResponseEntity<ClientMeDto> getUserDetails(HttpServletRequest request) {

        String username = this.clientService.getUserDetailsService(request);

        ClientMeDto user = clientService.findByEmail(username);

        return ResponseEntity.ok(user);
    }
    }

