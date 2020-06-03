package com.spbstu.dbo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public enum FormStatus {
    IN_WORK("В работе"),
    FINISHED("Завершена");

    private final String asString;

    FormStatus(String statusStr) {
        this.asString = statusStr;
    }

    @Override
    public String toString() {
        return asString;
    }

    private static final Map<String, FormStatus> LOOKUP_MAP = new HashMap<>();

    static {
        for (FormStatus v : values()) {
            LOOKUP_MAP.put(v.toString(), v);
        }
    }

    public static FormStatus getByStr(String str) {
        return LOOKUP_MAP.get(str);
    }
}
