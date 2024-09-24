package com.sistema.login.Exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailExistingExceptionTest {

    @Test
    void MessageTesteException() {
        String messageException = "Email Existing";
        EmailExistingException exception = new EmailExistingException(messageException);

        assertEquals(messageException, exception.getMessage());
    }
}