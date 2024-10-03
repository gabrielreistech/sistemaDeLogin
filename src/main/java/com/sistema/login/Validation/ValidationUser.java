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

/**
 * Componente responsável pela validação de dados de clientes durante
 * operações de cadastro e autenticação.
 * <p>
 * Esta classe valida o e-mail para garantir que não haja duplicatas,
 * verifica a integridade dos dados fornecidos e autentica
 * as credenciais de login dos clientes.
 */
@Component
public class ValidationUser {

    /**
     * Repositório para operações de persistência relacionadas a clientes.
     */
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Codificador de senhas utilizado para verificar a correspondência de senhas.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Valida se o e-mail fornecido já existe no sistema.
     *
     * @param clientDto DTO contendo as informações do cliente a ser cadastrado.
     * @return O mesmo {@link ClientDto} caso a validação seja bem-sucedida.
     * @throws EmailExistingException Se o e-mail já estiver registrado no sistema.
     */
    public ClientDto validationEmail(ClientDto clientDto) {
        // Verifica se o e-mail já está presente no repositório
        Optional<Client> userEmail = this.clientRepository.findByEmail(clientDto.getEmail());

        if (userEmail.isPresent()) {
            throw new EmailExistingException("E-mail already exists");
        }
        return clientDto;
    }

    /**
     * Valida os dados fornecidos no DTO do cliente para garantir que
     * não contenham números nos campos de nome.
     *
     * @param clientDto DTO contendo as informações do cliente a serem validadas.
     * @return O mesmo {@link ClientDto} caso a validação seja bem-sucedida.
     * @throws IllegalArgumentException Se algum dos campos de nome contiver números.
     */
    public ClientDto validationData(ClientDto clientDto) {
        final Pattern CONTAINS_NUMBER = Pattern.compile(".*\\d.*");
        final Pattern CONTAINS_LETTER = Pattern.compile(".*[a-zA-Z].*");
        final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("^\\+\\d+$");

        // Verifica se o primeiro nome contém números
        if (CONTAINS_NUMBER.matcher(clientDto.getFirstName()).find()) {
            throw new IllegalArgumentException("Invalid fields");
        }

        // Verifica se o último nome contém números
        if (CONTAINS_NUMBER.matcher(clientDto.getLastName()).find()) {
            throw new IllegalArgumentException("Invalid fields");
        }

        for(int i = 0; i < clientDto.getPhones().size(); i++){

            long number = clientDto.getPhones().get(i).getNumber();
            Integer area = clientDto.getPhones().get(i).getAreaCode();

            String numberString = String.valueOf(number);
            String areaString = String.valueOf(area);

            if(CONTAINS_LETTER.matcher(numberString).find()){
                throw new IllegalArgumentException("Invalid fields");
            }

            if(CONTAINS_LETTER.matcher(areaString).find()){
                throw new IllegalArgumentException("Invalid fields");
            }

            if(!COUNTRY_CODE_PATTERN.matcher(clientDto.getPhones().get(i).getCountryCode()).find()){
                throw new IllegalArgumentException("Invalid fields");
            }
        }
        return clientDto;

    }

    /**
     * Valida as credenciais de login fornecidas pelo cliente.
     * <p>
     * Este método verifica se o e-mail existe no sistema e se a senha fornecida
     * corresponde à senha armazenada.
     * </p>
     *
     * @param clientLoginDto DTO contendo as credenciais de login do cliente.
     * @return Um {@link ClientLoginDto} atualizado com as informações do cliente.
     * @throws IllegalArgumentException Se o e-mail não existir ou se a senha estiver incorreta.
     */
    public ClientLoginDto validationLogin(ClientLoginDto clientLoginDto) {
        // Busca o cliente pelo e-mail ou lança uma exceção se não encontrado
        Client client = this.clientRepository.findByEmail(clientLoginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid e-mail or password"));

        // Verifica se a senha fornecida corresponde à senha armazenada
        if (!passwordEncoder.matches(clientLoginDto.getPassword(), client.getPassword())) {
            throw new IllegalArgumentException("Invalid e-mail or password");
        }

        // Atualiza a data do último login
        client.setLastLogin(LocalDateTime.now());
        this.clientRepository.save(client);

        // Retorna um DTO atualizado com as informações do cliente
        return new ClientLoginDto(client);
    }
}
