package com.desafio.pitang.Controllers;

import com.desafio.pitang.Dtos.ClientDto;
import com.desafio.pitang.Dtos.ClientLoginDto;
import com.desafio.pitang.Models.Phone;
import com.desafio.pitang.Services.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private PublicController publicController;

    private ObjectMapper objectMapper;

    Phone phone;

    List<Phone> phones;

    ClientDto clientDto;

    ClientLoginDto clientLoginDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
        objectMapper = new ObjectMapper(); // Inicialização correta

        phone = new Phone();
        phone.setId(1L);
        phone.setNumber(99999999L);
        phone.setAreaCode((byte) 81);
        phone.setCountryCode("+55");

        phones = List.of(phone);

        clientDto = new ClientDto("Teste", "Mock", "testeMock@gmail.com", "123456", phones);

        clientLoginDto = new ClientLoginDto();
        clientLoginDto.setPassword(clientDto.getPassword());
        clientLoginDto.setEmail(clientDto.getEmail());
    }

    @Test
    void testCreate() throws Exception {

        String expectedToken = "some-token";

        when(clientService.create(any(ClientDto.class))).thenReturn(expectedToken);

        mockMvc.perform(post("/public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(clientService, times(1)).create(any(ClientDto.class));
    }

    @Test
    void testLogin() throws Exception{
        String expectedToken = "some-token";

        when(clientService.login(any(ClientLoginDto.class))).thenReturn(expectedToken);

        mockMvc.perform(post("/public/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientLoginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(clientService, times(1)).login(any(ClientLoginDto.class));
    }
}