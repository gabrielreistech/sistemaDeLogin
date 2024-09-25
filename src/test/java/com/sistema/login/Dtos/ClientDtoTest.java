package com.sistema.login.Dtos;

import com.sistema.login.Models.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClientDtoTest {

    Phone phone;

    ClientDto clientDto;

    List<Phone> phones;

    @BeforeEach
    void setUp() {
        phone = new Phone();
        phone.setId(1L);
        phone.setNumber(99999999L);
        phone.setAreaCode((byte) 81);
        phone.setCountryCode("+55");

        phones = List.of(phone);

        clientDto = new ClientDto("Teste", "Mock", "testeMock@gmail.com", "123456", phones);
    }

    @Test
    void testeConstruct(){

        assertEquals("Teste", clientDto.getFirstName());
        assertEquals("Mock", clientDto.getLastName());
        assertEquals("testeMock@gmail.com", clientDto.getEmail());
        assertEquals("123456", clientDto.getPassword());
        assertEquals("+55", phones.getFirst().getCountryCode());

    }


}