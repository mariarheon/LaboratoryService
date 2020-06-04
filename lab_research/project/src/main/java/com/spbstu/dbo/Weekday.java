package com.spbstu.dbo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public enum Weekday {
    Monday("пн", 2),
    Tuesday("вт", 3),
    Wednesday("ср", 4),
    Thursday("чт", 5),
    Friday("пт", 6),
    Saturday("сб", 7),
    Sunday("вс", 1);

    private final String asString;
    private final int weekdayNumber;

    Weekday(String roleStr, int weekdayNumber) {
        this.asString = roleStr;
        this.weekdayNumber = weekdayNumber;
    }

    @Override
    public String toString() {
        return asString;
    }

    public int getWeekdayNumber() {
        return weekdayNumber;
    }

    private static final Map<String, Weekday> LOOKUP_MAP1 = new HashMap<>();
    private static final Map<Integer, Weekday> LOOKUP_MAP2 = new HashMap<>();

    static {
        for (Weekday v : values()) {
            LOOKUP_MAP1.put(v.toString(), v);
        }
        for (Weekday v : values()) {
            LOOKUP_MAP2.put(v.getWeekdayNumber(), v);
        }
    }

    public static Weekday getByStr(String str) {
        return LOOKUP_MAP1.get(str);
    }
    public static Weekday getByNumber(int weekdayNumber) {
        return LOOKUP_MAP2.get(weekdayNumber);
    }
}
