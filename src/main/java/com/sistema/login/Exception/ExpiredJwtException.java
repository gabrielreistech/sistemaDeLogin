package com.sistema.login.Exception;

public class ExpiredJwtException extends RuntimeException {

    ExpiredJwtException(String msg){
        super(msg);
    }

}
