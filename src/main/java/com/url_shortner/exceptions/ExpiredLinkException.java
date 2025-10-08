package com.url_shortner.exceptions;

public class ExpiredLinkException extends RuntimeException{
    public ExpiredLinkException(String message) {
        super(message);
    }
}
