package com.sistema.login.Security;

import com.sistema.login.Models.Client;
import com.sistema.login.Repositorys.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Implementação do serviço de detalhes do usuário para autenticação.
 * Esta classe busca informações do usuário a partir de um repositório de clientes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Carrega os detalhes do usuário a partir do nome de usuário (email).
     *
     * @param username O nome de usuário (email) do cliente a ser carregado.
     * @return Um objeto UserDetails que contém as informações do cliente.
     * @throws UsernameNotFoundException Se o usuário não for encontrado no repositório.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Client> clientOptional = this.clientRepository.findByEmail(username);

        Client client = clientOptional.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return org.springframework.security.core.userdetails.User
                .withUsername(client.getEmail())
                .password(client.getPassword())
                .build();
    }
}
