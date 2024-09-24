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

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    ClientService clientService;

    @PostMapping("/signup")
    public ResponseEntity<String> create(@Valid @RequestBody ClientDto clientDto){
      String tokenClient = this.clientService.create(clientDto);
      return ResponseEntity.ok(tokenClient);
    }

    @PostMapping("/signin")
    public ResponseEntity<String> login(@Valid @RequestBody ClientLoginDto clientLoginDto){
        String clientLogin = this.clientService.login(clientLoginDto);
        return ResponseEntity.ok(clientLogin);
    }
}
