package com.spbstu.exceptions;

/**
 *
 */
public class TimeFormatException extends Exception {
    public TimeFormatException(String time) {
        super("Строка " + time + " не может быть преобразована во время в формате HH:mm");
    }
}
