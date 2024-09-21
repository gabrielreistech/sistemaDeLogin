package com.desafio.pitang.Exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StandardErrorTest {

    StandardError error;

    @BeforeEach
    void setUp() {
        error = new StandardError();
        error.setError("Erro");
        error.setPath("/Path");
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage("Message error");
        error.setTimestamp(Instant.now());
    }

    @Test
    void Construct(){
        Instant timeNow = Instant.now();
        error.setTimestamp(timeNow);

        assertEquals(timeNow ,error.getTimestamp());
    }

}