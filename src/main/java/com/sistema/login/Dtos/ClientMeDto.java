package com.sistema.login.Dtos;

import com.sistema.login.Models.Client;
import com.sistema.login.Models.Phone;
import java.time.LocalDateTime;
import java.util.List;

public class ClientMeDto {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private List<Phone> phones;

    private LocalDateTime create;

    private LocalDateTime lastLogin;

    public ClientMeDto(){}

    public ClientMeDto(Client client){
        firstName = client.getFirstName();
        lastName = client.getLastName();
        email = client.getEmail();
        password = client.getPassword();
        phones = client.getPhones();
        create = client.getCreate();
        lastLogin = client.getLastLogin();
    }

    public ClientMeDto(String firstName, String lastName, String email, String password, List<Phone> phones, LocalDateTime create, LocalDateTime lastLogin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phones = phones;
        this.create = create;
        this.lastLogin = lastLogin;
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

    public LocalDateTime getCreate() {
        return create;
    }

    public void setCreate(LocalDateTime create) {
        this.create = create;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
