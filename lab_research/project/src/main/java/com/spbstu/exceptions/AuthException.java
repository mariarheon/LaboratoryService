package com.spbstu.exceptions;

/**
 *
 */
public class AuthException extends Exception {
    public AuthException() {
        super("Неверный логин или пароль");
    }
}
