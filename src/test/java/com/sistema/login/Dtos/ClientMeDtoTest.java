package com.sistema.login.Dtos;


import com.sistema.login.Models.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClientMeDtoTest {

    Phone phone;

    Phone phone2;

    List<Phone> phones;

    List<Phone> phones2;

    ClientMeDto clientMeDto;

    @BeforeEach
    void setUp() {
        phone = new Phone();
        phone.setId(1L);
        phone.setNumber(99999999L);
        phone.setAreaCode((byte) 81);
        phone.setCountryCode("+55");

        phone2 = new Phone();
        phone2.setId(2L);
        phone2.setNumber(999999988L);
        phone2.setAreaCode((byte) 81);
        phone2.setCountryCode("+55");

        phones = List.of(phone);
        phones2 = List.of(phone2);

        clientMeDto = new ClientMeDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "securepassword",
                phones,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

        @Test
        void ConstructClientMeDto(){

            assertNotNull(clientMeDto);

            assertEquals("John", clientMeDto.getFirstName());
            assertEquals("Doe", clientMeDto.getLastName());
            assertEquals("john.doe@example.com", clientMeDto.getEmail());
            assertEquals("securepassword", clientMeDto.getPassword());
            assertEquals(1, clientMeDto.getPhones().size());
            assertEquals(phone, clientMeDto.getPhones().getFirst());

        }

        @Test
        void SetClientMeDtoTest(){
            clientMeDto.setFirstName("Kaio");
            clientMeDto.setLastName("Reis");
            clientMeDto.setEmail("kaio@gmail.com");
            clientMeDto.setPassword("123456");
            clientMeDto.setPhones(phones2);
            LocalDateTime timeLastLogin = LocalDateTime.now();
            clientMeDto.setLastLogin(timeLastLogin);
            LocalDateTime timeCreate = LocalDateTime.now();
            clientMeDto.setCreate(timeCreate);

            assertEquals("Kaio", clientMeDto.getFirstName());
            assertEquals("Reis", clientMeDto.getLastName());
            assertEquals("kaio@gmail.com", clientMeDto.getEmail());
            assertEquals("123456", clientMeDto.getPassword());
            assertEquals(2L, clientMeDto.getPhones().getFirst().getId());
            assertEquals(timeLastLogin, clientMeDto.getLastLogin());
            assertEquals(timeCreate, clientMeDto.getCreate());
        }

        @Test
        void ConstructEmpty(){
           ClientMeDto clientMe = new ClientMeDto();

           assertNotNull(clientMe);
           assertNull(clientMe.getEmail());
           assertNull(clientMe.getPassword());
        }
}