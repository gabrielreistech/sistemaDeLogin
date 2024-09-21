package com.desafio.pitang.Exception;

public class EmailExistingException extends IllegalArgumentException{

    String message;

    public EmailExistingException(String message){
        super(message);
    }

}
