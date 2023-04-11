package com.example.demo_scoala.exceptions;

public class NoFoundException extends RuntimeException{

    private String message;

    public NoFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
