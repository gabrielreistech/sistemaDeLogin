package com.sistema.login.Validation;

import com.sistema.login.Dtos.ClientDto;
import com.sistema.login.Dtos.ClientLoginDto;
import com.sistema.login.Exception.EmailExistingException;
import com.sistema.login.Models.Client;
import com.sistema.login.Repositorys.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class ValidationUser {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ClientDto validationEmail(ClientDto clientDto){
       Optional<Client> userEmail = this.clientRepository.findByEmail(clientDto.getEmail());

       if(userEmail.isPresent()){
           throw new EmailExistingException("E-mail already exists");
       }
       return clientDto;
    }

    public ClientDto validationData(ClientDto clientDto){

        final Pattern CONTAINS_NUMBER = Pattern.compile(".*\\d.*");

        if(CONTAINS_NUMBER.matcher(clientDto.getFirstName()).matches()){
            throw new IllegalArgumentException("Invalid fields");
        }

        if(CONTAINS_NUMBER.matcher(clientDto.getLastName()).matches()){
            throw new IllegalArgumentException("Invalid fields");
        }
        return clientDto;
    }

    public ClientLoginDto validationLogin(ClientLoginDto clientLoginDto){
       Client client = this.clientRepository.findByEmail(clientLoginDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid e-mail or password"));

       if(!passwordEncoder.matches(clientLoginDto.getPassword(), client.getPassword())){
           throw new IllegalArgumentException("Invalid e-mail or password");
       }

       client.setLastLogin(LocalDateTime.now());
       this.clientRepository.save(client);

           return new ClientLoginDto(client);
    }
}
