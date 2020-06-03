package com.spbstu.exceptions;

/**
 *
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String login) {
        super("Пользователь с логином " + login + " не найден");
    }
}
