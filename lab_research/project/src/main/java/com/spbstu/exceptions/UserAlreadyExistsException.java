package com.spbstu.exceptions;

/**
 *
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String login) {
        super("Пользователь с логином " + login + " уже существует");
    }
}
