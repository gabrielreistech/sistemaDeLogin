package com.sistema.login.Dtos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClientLoginDtoTest {

    ClientLoginDto clientLoginDto = new ClientLoginDto("testeLogin@gmail.com","123456");


    @Test
    void ClientLoginDto(){
        clientLoginDto.setEmail("mudei@gmail.com");
        clientLoginDto.setPassword("mudei123");

        assertEquals("mudei@gmail.com", clientLoginDto.getEmail());
        assertEquals("mudei123", clientLoginDto.getPassword());
    }

    @Test
    void ConstructEmpty(){
        ClientLoginDto clientEmpty = new ClientLoginDto();

        assertNotNull(clientEmpty);
        assertNull(clientEmpty.getPassword());
        assertNull(clientEmpty.getEmail());
    }
}