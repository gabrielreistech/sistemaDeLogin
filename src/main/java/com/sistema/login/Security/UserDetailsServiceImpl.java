package com.desafio.pitang.Security;


import com.desafio.pitang.Models.Client;
import com.desafio.pitang.Repositorys.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Client> clientOptional = this.clientRepository.findByEmail(username);

        Client client = clientOptional.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Converte a entidade User para UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(client.getEmail())
                .password(client.getPassword())
                .build();
    }
}
