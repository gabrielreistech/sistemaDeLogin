package com.sistema.login.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long number;

    @JsonProperty("area_code")
    private Byte areaCode;

    @JsonProperty("country_code")
    private String countryCode;

    public Phone(){}

    public Phone(Long number, Byte areaCode, String countryCode) {
        this.number = number;
        this.areaCode = areaCode;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Byte getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Byte areaCode) {
        this.areaCode = areaCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
