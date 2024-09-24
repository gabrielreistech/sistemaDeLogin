package com.sistema.login.Services;

import com.sistema.login.Dtos.ClientDto;
import com.sistema.login.Dtos.ClientLoginDto;
import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Models.Client;
import com.sistema.login.Repositorys.ClientRepository;
import com.sistema.login.Security.JwtUtil;
import com.sistema.login.Validation.ValidationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ValidationUser validationUser;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    public String create(ClientDto clientDto){

        validationUser.validationEmail(clientDto);
        validationUser.validationData(clientDto);

        String encoder = this.passwordEncoder.encode(clientDto.getPassword());

        Client client = new Client(clientDto);
        client.setPassword(encoder);
        client.setCreate(LocalDateTime.now());
        client.setLastLogin(LocalDateTime.now());

        this.clientRepository.save(client);

        return jwtUtil.generateToken(client.getEmail());
    }

    public ClientMeDto findByEmail(String userName){
       Client client = this.clientRepository.findByEmail(userName).orElseThrow(() -> new IllegalArgumentException("User not Found"));
       return new ClientMeDto(client);
    }

    public String login(ClientLoginDto clientLoginDto){
        ClientLoginDto client = validationUser.validationLogin(clientLoginDto);
        return jwtUtil.generateToken(client.getEmail());
    }
}
