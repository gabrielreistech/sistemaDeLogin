package com.sistema.login.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Security.TestSecurityConfig;
import com.sistema.login.Services.ClientService;
import com.sistema.login.Security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
public class PrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtUtil jwtUtil;

    private ClientMeDto clientMeDto;

    @BeforeEach
    void setUp() {
        clientMeDto = new ClientMeDto();
        clientMeDto.setEmail("user@example.com"); // Configure outros campos conforme necessário
    }

    @Test
    void testGetUserDetails_Unauthorized_NoToken() throws Exception {
        mockMvc.perform(get("/private/me")) // Sem cabeçalho
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void testGetUserDetails_InvalidAuthHeader() throws Exception {
        // Testa com cabeçalho Authorization nulo
        mockMvc.perform(get("/private/me")) // Sem cabeçalho
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));

        // Testa com cabeçalho Authorization que não começa com Bearer
        String invalidAuthHeader = "Basic some_other_token"; // Cabeçalho inválido
        mockMvc.perform(get("/private/me").header("Authorization", invalidAuthHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void testGetUserDetails_Unauthorized_InvalidToken() throws Exception {
        String invalidToken = "invalid_token"; // Token inválido

        // Simulando o comportamento do método extractUsername para o token inválido
        when(jwtUtil.extractUsername("invalid_token")).thenThrow(new RuntimeException("Invalid token"));

        // Executando a requisição com o cabeçalho Authorization
        mockMvc.perform(get("/private/me").header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void testGetUserDetails_Success() throws Exception {
        String validToken = "valid_token";
        String username = clientMeDto.getEmail();

        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(clientService.findByEmail(username)).thenReturn(clientMeDto);

        mockMvc.perform(get("/private/me").header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(clientMeDto)));
    }

    @Test
    void getUserDetails_InvalidToken_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        String authHeader = "Bearer invalid_token";
        when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        mockMvc.perform(get("/private/me")
                        .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void testGetUserDetails_AllUnauthorizedCases() throws Exception {

        mockMvc.perform(get("/private/me")) // Sem cabeçalho
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));

        String invalidAuthHeader = "Basic some_other_token"; // Cabeçalho inválido
        mockMvc.perform(get("/private/me").header("Authorization", invalidAuthHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));

        String validAuthHeader = "Bearer valid_token";
        when(jwtUtil.extractUsername("valid_token")).thenThrow(new RuntimeException("Erro ao extrair o nome de usuário"));

        mockMvc.perform(get("/private/me").header("Authorization", validAuthHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }
}
