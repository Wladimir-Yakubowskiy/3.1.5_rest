package ru.kata.spring.boot_security.demo.util;

public class UserCreatingOrUpdatingException extends RuntimeException{
    public UserCreatingOrUpdatingException(String msg) {
        super(msg);
    }
}
