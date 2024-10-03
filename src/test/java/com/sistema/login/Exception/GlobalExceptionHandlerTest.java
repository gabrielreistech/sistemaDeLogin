package com.sistema.login.Exception;

import com.fasterxml.jackson.core.JsonParseException;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {


    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testEmailExistingExceptionHandler() {

        String message = "Email Existing";
        EmailExistingException exception = new EmailExistingException(message);

        ResponseEntity<StandardError> response = handler.emailExisting(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(message, response.getBody().getMessage());
        assertEquals("N/A", response.getBody().getPath());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertEquals("Email Existing", response.getBody().getError());
    }

    @Test
    void testHandleJsonParseException() {

        JsonParseException ex = new JsonParseException("Invalid JSON");

        ResponseEntity<StandardError> response = handler.handleJsonParseException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid fields", response.getBody().getMessage());
        assertEquals("JSON Parse Error", response.getBody().getError());
    }

    @Test
    void testHandleValidationExceptions() {
        MethodParameter methodParameter = Mockito.mock(MethodParameter.class);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");
        bindingResult.addError(fieldError);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<StandardError> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid fields", response.getBody().getMessage());
        assertEquals("Validation error", response.getBody().getError());
    }

    @Test
    void testHandleGeneralExceptions() {
        Exception ex = new Exception("General error");

        ResponseEntity<StandardError> response = handler.handleGeneralExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("General error", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
}