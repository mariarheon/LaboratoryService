package com.spbstu.dbo;

import com.spbstu.exceptions.TimeFormatException;

import java.util.Calendar;

/**
 *
 */
public class Time {
    private int hour;
    private int minute;

    public Time() {
        hour = 0;
        minute = 0;
    }

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(int totalMinutes) {
        this.hour = totalMinutes / 60;
        this.minute = totalMinutes % 60;
    }

    public Time(Time other) {
        this.hour = other.getHour();
        this.minute = other.getMinute();
    }

    public Time(java.util.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.minute = c.get(Calendar.MINUTE);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getTotalMinutes() {
        return hour * 60 + minute;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }

    public static Time parse(String str) throws TimeFormatException {
        if (str.length() != 5 || str.charAt(2) != ':') {
            throw new TimeFormatException(str);
        }
        char h1 = str.charAt(0);
        char h2 = str.charAt(1);
        char m1 = str.charAt(3);
        char m2 = str.charAt(4);
        if (!Character.isDigit(h1)
                || !Character.isDigit(h2)
                || !Character.isDigit(m1)
                || !Character.isDigit(m2)) {
            throw new TimeFormatException(str);
        }
        int hour = Character.digit(h1, 10) * 10 + Character.digit(h2, 10);
        int min = Character.digit(m1, 10) * 10 + Character.digit(m2, 10);
        if (hour > 23 || min > 59) {
            throw new TimeFormatException(str);
        }
        return new Time(hour, min);
    }

    public boolean beforeOrEqual(Time other) {
        return getTotalMinutes() <= other.getTotalMinutes();
    }

    public boolean afterOrEqual(Time other) {
        return getTotalMinutes() >= other.getTotalMinutes();
    }

    public boolean before(Time other) {
        return getTotalMinutes() < other.getTotalMinutes();
    }

    public boolean after(Time other) {
        return getTotalMinutes() > other.getTotalMinutes();
    }
}
