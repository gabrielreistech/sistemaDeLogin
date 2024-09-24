package com.sistema.login.Models;

import com.sistema.login.Dtos.ClientDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Phone> phones;

    @JsonIgnore
    private LocalDateTime create;

    @JsonIgnore
    private LocalDateTime lastLogin;

    public Client(){}

    public Client(String firstName, String lastName, String email, String password, List<Phone> phones, LocalDateTime create, LocalDateTime lastLogin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phones = phones;
        this.create = create;
        this.lastLogin = lastLogin;
    }

    public Client(ClientDto userDto){
        firstName = userDto.getFirstName();
        lastName = userDto.getLastName();
        email = userDto.getEmail();
        password = userDto.getPassword();
        phones = userDto.getPhones();
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
