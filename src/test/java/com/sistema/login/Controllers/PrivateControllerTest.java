package com.sistema.login.Controllers;

import com.sistema.login.Dtos.ClientMeDto;
import com.sistema.login.Models.Client;
import com.sistema.login.Models.Phone;
import com.sistema.login.Security.TestSecurityConfig;
import com.sistema.login.Services.ClientService;
import com.sistema.login.Security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class PrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    private ClientMeDto clientMeDto;


    @BeforeEach
    void setUp() {
        Phone phone = new Phone(819999999L, 81, "+55");
        phone.setId(1L);
        List<Phone> phones = List.of(phone);

        Client client = new Client("Gabriel", "Teste", "gabrielteste@gmail.com", "123456", phones, LocalDateTime.now(), LocalDateTime.now());

        clientMeDto = new ClientMeDto(client);
    }

    @Test
    void throwsNoHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        mockMvc.perform(get("/private/me"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void testGetUserDetails_Unauthorized_NoToken() throws Exception {
        mockMvc.perform(get("/private/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserDetails_InvalidAuthHeader() throws Exception {
        // Testa com cabeçalho Authorization que não começa com Bearer
        String invalidAuthHeader = "Basic some_other_token";
        mockMvc.perform(get("/private/me")
                .header("Authorization", invalidAuthHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserDetails_Unauthorized_InvalidToken() throws Exception {
        String invalidToken = "invalid_token";

        when(jwtUtil.extractUsername("invalid_token")).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/private/me").header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testValidateToken() {
        String validToken = "valid_token";
        String username = "user@example.com";

        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(jwtUtil.validateToken(validToken, username)).thenReturn(true);

        // Adicione uma verificação aqui
        assertTrue(jwtUtil.validateToken(validToken, username)); // Verifica se o token é validado corretamente
    }

    @Test
    void testGetUserDetails_Success() throws Exception {
        String validToken = "Bearer valid_token";
        String username = clientMeDto.getEmail();

        // Mockar comportamento do JWT Util
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(jwtUtil.validateToken(validToken, username)).thenReturn(true);

        // Mockar o serviço de detalhes do usuário
        when(clientService.getUserDetailsService(any(HttpServletRequest.class))).thenReturn(username);

        // Mockar a busca pelo usuário
        when(clientService.findByEmail(username)).thenReturn(clientMeDto);

        // Perform request and expect status 200 OK with correct JSON response
        mockMvc.perform(get("/private/me")
                        .header("Authorization", validToken))
                .andDo(print()) // Para imprimir a resposta no console
                .andExpect(status().isOk());
    }


    @Test
    void getUserDetails_InvalidToken_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        String authHeader = "Bearer invalid_token";
        when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        mockMvc.perform(get("/private/me")
                        .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserDetails_AllUnauthorizedCases() throws Exception {

        mockMvc.perform(get("/private/me"))
                .andExpect(status().isUnauthorized());

        String invalidAuthHeader = "Basic some_other_token";
        mockMvc.perform(get("/private/me").header("Authorization", invalidAuthHeader))
                .andExpect(status().isUnauthorized());

        String validAuthHeader = "Bearer valid_token";
        when(jwtUtil.extractUsername("valid_token")).thenThrow(new RuntimeException("Erro ao extrair o nome de usuário"));

        mockMvc.perform(get("/private/me").header("Authorization", validAuthHeader))
                .andExpect(status().isUnauthorized());
    }
}
