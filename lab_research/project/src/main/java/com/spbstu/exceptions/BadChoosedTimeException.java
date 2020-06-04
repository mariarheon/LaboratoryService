package com.spbstu.exceptions;

/**
 *
 */
public class BadChoosedTimeException extends Exception {
    public BadChoosedTimeException() {
        super("Все ассистенты в выбранное время заняты.");
    }
}
