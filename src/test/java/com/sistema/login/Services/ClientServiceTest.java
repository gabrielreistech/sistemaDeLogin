package com.sistema.login.Services;

import com.sistema.login.Dtos.ClientDto;
import com.sistema.login.Dtos.ClientLoginDto;
import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Exception.EmailExistingException;
import com.sistema.login.Models.Client;
import com.sistema.login.Models.Phone;
import com.sistema.login.Repositorys.ClientRepository;
import com.sistema.login.Security.JwtUtil;
import com.sistema.login.Validation.ValidationUser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Mock
    HttpServletRequest request;

    private Client client;

    private ClientDto clientDto;

    private ClientLoginDto clientLoginDto;

    @BeforeEach
    void setUp(){

        Phone phone = new Phone();
        phone.setId(1L);
        phone.setNumber(985958595L);
        phone.setAreaCode(81);
        phone.setCountryCode("+55");

        Phone phone2 = new Phone();
        phone.setId(2L);
        phone.setNumber(995958595L);
        phone.setAreaCode(81);
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

    @Test
    void getUserDetailsService() {
         String authHeaderMock = "Bearer mock";
         String tokenMock = "mock";
         String userNameMock = "userNameMock@gmail.com";

         when(request.getHeader("Authorization")).thenReturn(authHeaderMock);
         when(jwtUtil.extractUsername(tokenMock)).thenReturn(userNameMock);

         String username = clientService.getUserDetailsService(request);

         assertEquals(username, userNameMock);

         verify(request, times(1)).getHeader("Authorization");

    }

    @Test
    void getUserDetailsServiceException(){
        String authHeaderMock = null;
        when(request.getHeader("Authorization")).thenReturn(authHeaderMock);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> clientService.getUserDetailsService(request));

        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void getUserDetailsServiceExceptionTwo(){
        String authHeaderMock = "SemBearer";
        when(request.getHeader("Authorization")).thenReturn(authHeaderMock);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> clientService.getUserDetailsService(request));

        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void getUserDetailsService_ThrowsException() {
        String authHeaderMock = "Bearer mockToken";
        String tokenMock = "mockToken";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(authHeaderMock);

        when(jwtUtil.extractUsername(tokenMock)).thenThrow(new RuntimeException("Token inválido"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.getUserDetailsService(request);
        });

        assertEquals("Unauthorized", exception.getMessage());
        verify(request, times(1)).getHeader("Authorization");
    }
}