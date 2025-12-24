package com.smartusers.logitrackapi.Exception;


public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}

