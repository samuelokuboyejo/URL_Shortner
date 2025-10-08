package com.url_shortner.exceptions;

public class InvalidURLException extends RuntimeException  {
    public InvalidURLException(String message) {
        super(message);
    }

}
