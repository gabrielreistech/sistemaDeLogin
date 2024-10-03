package com.sistema.login.Security;

import com.sistema.login.Models.Client;
import com.sistema.login.Models.Phone;
import com.sistema.login.Repositorys.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void loadUserByUsername_ReturnsUserDetails_WhenUserExists() {

        Phone phone = new Phone(819999999L, 81, "+55");
        phone.setId(1L);
        List<Phone> phones = List.of(phone);

        Client client = new Client("Gabriel", "Teste", "gabrielteste@gmail.com", "123456", phones, LocalDateTime.now(), LocalDateTime.now());


        when(clientRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        UserDetails userDetails = userDetailsService.loadUserByUsername(client.getEmail());

        assertNotNull(userDetails);
        assertEquals(client.getEmail(), userDetails.getUsername());
    }

    @Test
    public void loadUserByUsername_ThrowsException_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";

        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }
}