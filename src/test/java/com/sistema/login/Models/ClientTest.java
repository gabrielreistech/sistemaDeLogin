package com.sistema.login.Models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClientTest {

    @Test
    void createConstruct(){
        Phone phone = new Phone(819999999L, (byte)81, "+55");
        phone.setId(1L);
        List<Phone> phones = List.of(phone);

        Client client = new Client("Gabriel", "Teste", "gabrielteste@gmail.com", "123456", phones, LocalDateTime.now(), LocalDateTime.now());

        assertEquals("123456", client.getPassword());
        assertEquals(1L, phone.getId());
        assertEquals(819999999L, phone.getNumber());
        assertEquals((byte) 81, phone.getAreaCode());
        assertEquals("+55", phone.getCountryCode());
    }
}