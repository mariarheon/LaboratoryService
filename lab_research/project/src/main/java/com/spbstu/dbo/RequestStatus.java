package com.spbstu.dbo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public enum RequestStatus {
    CREATED("На рассмотрении"),
    APPLIED("Принята"),
    IN_WORK("В работе"),
    CANCELED("Отклонена"),
    FINISHED("Завершена"),
    CLIENT_IS_AWARE("Клиент осведомлен");

    private final String asString;

    RequestStatus(String roleStr) {
        this.asString = roleStr;
    }

    @Override
    public String toString() {
        return asString;
    }

    private static final Map<String, RequestStatus> LOOKUP_MAP = new HashMap<>();

    static {
        for (RequestStatus v : values()) {
            LOOKUP_MAP.put(v.toString(), v);
        }
    }

    public static RequestStatus getByStr(String str) {
        return LOOKUP_MAP.get(str);
    }
}