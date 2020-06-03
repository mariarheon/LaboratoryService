package com.spbstu.dbo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public enum Sex {
    Female("Женский"),
    Male("Мужской");

    private final String asString;

    Sex(String roleStr) {
        this.asString = roleStr;
    }

    @Override
    public String toString() {
        return asString;
    }

    private static final Map<String, Sex> LOOKUP_MAP = new HashMap<>();

    static {
        for (Sex v : values()) {
            LOOKUP_MAP.put(v.toString(), v);
        }
    }

    public static Sex getByStr(String str) {
        return LOOKUP_MAP.get(str);
    }
}