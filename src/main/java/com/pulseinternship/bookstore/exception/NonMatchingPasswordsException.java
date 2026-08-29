package com.pulseinternship.bookstore.exception;

public class NonMatchingPasswordsException extends RuntimeException {
    public NonMatchingPasswordsException(String message) {
        super(message);
    }
}
