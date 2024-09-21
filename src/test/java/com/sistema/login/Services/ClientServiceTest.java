package com.desafio.pitang.Services;

import com.desafio.pitang.Dtos.ClientDto;
import com.desafio.pitang.Dtos.ClientLoginDto;
import com.desafio.pitang.Dtos.ClientMeDto;
import com.desafio.pitang.Exception.EmailExistingException;
import com.desafio.pitang.Models.Client;
import com.desafio.pitang.Models.Phone;
import com.desafio.pitang.Repositorys.ClientRepository;
import com.desafio.pitang.Security.JwtUtil;
import com.desafio.pitang.Validation.ValidationUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    ValidationUser validationUser;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    ClientService clientService;

    private Client client;

    private ClientDto clientDto;

    private ClientLoginDto clientLoginDto;

    @BeforeEach
    void setUp(){

        Phone phone = new Phone();
        phone.setId(1L);
        phone.setNumber(985958595L);
        phone.setAreaCode((byte)81);
        phone.setCountryCode("+55");

        Phone phone2 = new Phone();
        phone.setId(2L);
        phone.setNumber(995958595L);
        phone.setAreaCode((byte)81);
        phone.setCountryCode("+55");

        List<Phone> phones = Arrays.asList(phone, phone2);

        clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("Teste");
        clientDto.setLastName("Mock");
        clientDto.setEmail("gabrielteste@gmail.com");
        clientDto.setPassword("123456");
        clientDto.setPhones(phones);

        client = new Client(clientDto);

        clientLoginDto = new ClientLoginDto("gabrielteste@gmail.com", "123456");

    }

    @Test
    void create() {

        when(passwordEncoder.encode(anyString())).thenReturn("encoder-password");

        when(validationUser.validationEmail(clientDto)).thenReturn(clientDto);
        when(validationUser.validationData(clientDto)).thenReturn(clientDto);

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        String expectedToken = "mocked-jwt-token";
        when(jwtUtil.generateToken(client.getEmail())).thenReturn(expectedToken);

        String result = clientService.create(clientDto);

        verify(clientRepository).save(any(Client.class));
        verify(jwtUtil).generateToken(clientDto.getEmail());

        assertEquals(expectedToken, result);
    }

    @Test
    void findByEmail() {
        when(clientRepository.findByEmail(clientDto.getEmail())).thenReturn(Optional.ofNullable(client));

        ClientMeDto clientMeDto = clientService.findByEmail(clientDto.getEmail());

        assertNotNull(clientMeDto);
        assertEquals("gabrielteste@gmail.com", clientMeDto.getEmail());
    }

    @Test
    void findAll() {
        Client client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("Gabriel");
        client1.setLastName("Antunes");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setFirstName("John");
        client2.setLastName("Doe");

        List<Client> clients = Arrays.asList(client1, client2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("firstName"));

        Page<Client> pageResponse = new PageImpl<>(clients, pageable, clients.size());
        when(clientRepository.findAll(pageable)).thenReturn(pageResponse);

        Page<ClientDto> result = clientService.findAll(0, 10, "firstName");

        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("Gabriel", result.getContent().get(0).getFirstName());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("John", result.getContent().get(1).getFirstName());
    }

    @Test
    void login() {

        String expectedToken = "mocked-jwt-token";
        when(validationUser.validationLogin(clientLoginDto)).thenReturn(clientLoginDto);
        when(jwtUtil.generateToken(clientLoginDto.getEmail())).thenReturn(expectedToken);

        String result = clientService.login(clientLoginDto);

        assertEquals(expectedToken, result);

        verify(validationUser).validationLogin(clientLoginDto);
        verify(jwtUtil).generateToken(clientLoginDto.getEmail());
    }

    @Test
    void createEmailExistingException(){
        when(validationUser.validationEmail(clientDto)).thenThrow(new EmailExistingException("E-mail already exists"));

        EmailExistingException exception = assertThrows(EmailExistingException.class, () -> clientService.create(clientDto));

        assertEquals("E-mail already exists", exception.getMessage());
    }

    @Test
    void CreateEmailErrorData(){
        when(validationUser.validationData(clientDto)).thenThrow(new IllegalArgumentException("Invalid fields"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> clientService.create(clientDto));

        assertEquals("Invalid fields", exception.getMessage());
    }

    @Test
    void testFindByEmail(){
        String username = client.getEmail();

        when(clientRepository.findByEmail(username)).thenReturn(Optional.of(client));

        clientService.findByEmail(username);

        assertEquals("gabrielteste@gmail.com", client.getEmail());
        assertEquals(username, client.getEmail());
    }

    @Test
    void testFindByEmailException() {
        String userName = client.getEmail();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> clientService.findByEmail(userName));

        assertEquals("User not Found", exception.getMessage());
    }
}