package com.desafio.pitang.Dtos;

import com.desafio.pitang.Models.Client;
import com.desafio.pitang.Models.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ClientDto {

    private Long id;

    @NotBlank(message = "Missing fields")
    private String firstName;

    @NotBlank(message = "Missing fields")
    private String lastName;

    @NotBlank(message = "Missing fields")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Missing fields")
    private String password;

    @NotEmpty(message = "Missing fields")
    private List<Phone> phones;

    public ClientDto(){}

    public ClientDto(Client client){
        id = client.getId();
        firstName = client.getFirstName();
        lastName = client.getLastName();
        email = client.getEmail();
        password = client.getPassword();
        phones = client.getPhones();
    }

    public ClientDto(String firstName, String lastName, String email, String password, List<Phone> phones) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phones = phones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }
}
