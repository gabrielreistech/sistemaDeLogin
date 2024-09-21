package com.desafio.pitang.Dtos;

import com.desafio.pitang.Models.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ClientLoginDto {

    @Email(message = "Invalid Email")
    @NotBlank(message = "Missing fields")
    private String email;

    @NotBlank(message = "Missing fields")
    private String password;

    public ClientLoginDto(){}

    public ClientLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public ClientLoginDto(Client client) {
        email = client.getEmail();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
