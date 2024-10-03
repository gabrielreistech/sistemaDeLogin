package com.sistema.login.Services;

import com.sistema.login.Dtos.ClientDto;
import com.sistema.login.Dtos.ClientLoginDto;
import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Exception.ExpiredJwtException;
import com.sistema.login.Models.Client;
import com.sistema.login.Repositorys.ClientRepository;
import com.sistema.login.Security.JwtUtil;
import com.sistema.login.Validation.ValidationUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço responsável pelas operações relacionadas aos clientes, incluindo
 * cadastro, autenticação e recuperação de detalhes do cliente.
 * <p>
 * Esta classe gerencia a interação com o repositório de clientes, valida
 * dados de entrada e gera tokens JWT para autenticação.
 *
 */
@Service
public class ClientService {

    /**
     * Repositório para operações de persistência relacionadas a clientes.
     */
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Serviço de validação de usuários, responsável por validar
     * e-mails, dados de cadastro e credenciais de login.
     */
    @Autowired
    private ValidationUser validationUser;

    /**
     * Utilitário para geração e validação de tokens JWT.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Codificador de senhas utilizado para encriptar senhas de clientes.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Realiza o cadastro de um novo cliente.
     * <p>
     * Este método valida os dados do cliente, encripta a senha, define as
     * datas de criação e último login, persiste o cliente no repositório
     * e gera um token JWT para autenticação.
     * </p>
     *
     * @param clientDto DTO contendo as informações do cliente a ser cadastrado.
     * @return Um token JWT válido para o cliente recém-cadastrado.
     * @throws IllegalArgumentException Se a validação do e-mail ou dados falhar.
     */
    @Transactional
    public String create(ClientDto clientDto) {
        // Valida o e-mail e os dados do cliente
        validationUser.validationEmail(clientDto);
        validationUser.validationData(clientDto);

        // Encripta a senha do cliente
        String encoder = this.passwordEncoder.encode(clientDto.getPassword());

        // Cria uma nova instância de Client com os dados fornecidos
        Client client = new Client(clientDto);
        client.setPassword(encoder);
        client.setCreate(LocalDateTime.now());
        client.setLastLogin(LocalDateTime.now());

        // Salva o cliente no repositório
        this.clientRepository.save(client);

        // Gera um token JWT para o cliente
        return jwtUtil.generateToken(client.getEmail());
    }

    /**
     * Recupera os detalhes de um cliente com base no e-mail fornecido.
     *
     * @param userName O e-mail do cliente cujo detalhe será recuperado.
     * @return Um DTO contendo os detalhes do cliente.
     * @throws IllegalArgumentException Se nenhum cliente for encontrado com o e-mail fornecido.
     */
    @Transactional
    public ClientMeDto findByEmail(String userName) {
        // Busca o cliente pelo e-mail ou lança uma exceção se não encontrado
        Client client = this.clientRepository.findByEmail(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not Found"));
        return new ClientMeDto(client);
    }

    /**
     * Autentica um cliente existente com base nas credenciais fornecidas.
     *
     * @param clientLoginDto DTO contendo as credenciais de login do cliente.
     * @return Um token JWT válido para o cliente autenticado.
     * @throws IllegalArgumentException Se a validação do login falhar.
     */
    @Transactional
    public String login(ClientLoginDto clientLoginDto) {
        // Valida as credenciais de login e retorna o DTO validado
        ClientLoginDto client = validationUser.validationLogin(clientLoginDto);
        // Gera um token JWT para o cliente autenticado
        return jwtUtil.generateToken(client.getEmail());
    }

    /**
     * Obtém o nome de usuário (e-mail) a partir do token JWT presente na requisição.
     *
     * @param request A requisição HTTP contendo o cabeçalho de autorização.
     * @return O e-mail do usuário autenticado.
     * @throws IllegalArgumentException Se o token estiver ausente, malformado ou inválido.
     */
    @Transactional
    public String getUserDetailsService(HttpServletRequest request) {
        // Extrai o cabeçalho de autorização da requisição
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Unauthorized"); // Lançar uma exceção se não houver token
        }

        // Extrai o token JWT do cabeçalho
        String token = authHeader.substring(7); // Remove "Bearer "
        String username;

        try {
            // Extrai o nome de usuário (e-mail) do token JWT
            username = jwtUtil.extractUsername(token);
            if (username == null) {
                throw new IllegalArgumentException("Unauthorized");
            }
        } catch (ExpiredJwtException e) {
            // Captura a exceção específica de token expirado
            throw new IllegalArgumentException("Unauthorized - invalid session");
        } catch (Exception e) {
            // Captura qualquer outra exceção
            throw new IllegalArgumentException("Unauthorized");
        }

        return username; // Retorna o nome de usuário extraído
    }
}