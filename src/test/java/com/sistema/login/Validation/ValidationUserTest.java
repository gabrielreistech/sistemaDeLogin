package com.sistema.login.Validation;

import com.sistema.login.Dtos.ClientDto;
import com.sistema.login.Dtos.ClientLoginDto;
import com.sistema.login.Exception.EmailExistingException;
import com.sistema.login.Models.Client;
import com.sistema.login.Models.Phone;
import com.sistema.login.Repositorys.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationUserTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    ValidationUser validationUser;

    Phone phone;

    Client client;

    Client clientNovo;

    ClientDto clientDto;

    ClientDto clientDtoNovo;

    ClientLoginDto clientLoginDto;

    @BeforeEach
    void setUp(){
        phone = new Phone();
        phone.setId(1L);
        phone.setNumber(985958595L);
        phone.setAreaCode((byte)81);
        phone.setCountryCode("+55");

        List<Phone> phones = List.of(phone);

        client = new Client();
        client.setId(1L);
        client.setFirstName("Teste");
        client.setLastName("Mock");
        client.setEmail("gabrielteste@gmail.com");
        client.setPassword("123456");
        client.setPhones(phones);

        clientNovo = new Client();
        clientNovo.setId(2L);
        clientNovo.setFirstName("Teste2");
        clientNovo.setLastName("Mock2");
        clientNovo.setEmail("gabrielteste2@gmail.com");
        clientNovo.setPassword("1234562");
        clientNovo.setPhones(phones);

        clientDto = new ClientDto(client);

        clientDtoNovo = new ClientDto(clientNovo);

        clientLoginDto = new ClientLoginDto(client.getEmail(), client.getPassword());
    }

    @Test
    void testValidationLoginEncoderTrue() {
        when(clientRepository.findByEmail(clientLoginDto.getEmail())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(clientLoginDto.getPassword(), client.getPassword())).thenReturn(true);

        validationUser.validationLogin(clientLoginDto);

        verify(clientRepository, times(1)).findByEmail(clientLoginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(clientLoginDto.getPassword(), client.getPassword());
    }

    @Test
    void validationLoginEncoderFalse() {
        when(clientRepository.findByEmail(clientLoginDto.getEmail())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(clientLoginDto.getPassword(), client.getPassword())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationUser.validationLogin(clientLoginDto));

        assertEquals("Invalid e-mail or password", exception.getMessage());
    }

    @Test
    void validationLoginFindByEmailException(){
        when(clientRepository.findByEmail(clientLoginDto.getEmail())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationUser.validationLogin(clientLoginDto));

        assertEquals("Invalid e-mail or password", exception.getMessage());
    }

    @Test
    void validationEmail() {

        when(clientRepository.findByEmail(clientDtoNovo.getEmail())).thenReturn(Optional.empty());

        validationUser.validationEmail(clientDtoNovo);

        verify(clientRepository, times(1)).findByEmail(clientDtoNovo.getEmail());

    }

    @Test
    void validationEmailException(){
        when(clientRepository.findByEmail(clientDtoNovo.getEmail())).thenReturn(Optional.of(client));

        EmailExistingException exception = assertThrows(EmailExistingException.class, () -> validationUser.validationEmail(clientDtoNovo));

        assertEquals("E-mail already exists", exception.getMessage());
    }


    @Test
    void validationData() {
        ClientDto result = validationUser.validationData(clientDto);
        assertEquals(result, clientDto);
    }

    @Test
    void validationDataFirstNameException(){
        clientDtoNovo.setFirstName("John123");
        clientDtoNovo.setLastName("Teste");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationUser.validationData(clientDtoNovo));

        assertEquals("Invalid fields", exception.getMessage());
    }

    @Test
    void validationDataLastNameException(){
        clientDtoNovo.setFirstName("John");
        clientDtoNovo.setLastName("Teste123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validationUser.validationData(clientDtoNovo));

        assertEquals("Invalid fields", exception.getMessage());
    }

}