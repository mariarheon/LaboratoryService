package com.spbstu.dbo;

import java.util.List;

/**
 *
 */
public class AvailableAssistant {
    private Assistant assistant;
    private List<TimeSpan> availableTimeSpanList;

    public AvailableAssistant(Assistant assistant, List<TimeSpan> availableTimeSpanList) {
        this.assistant = assistant;
        this.availableTimeSpanList = availableTimeSpanList;
    }

    public Assistant getAssistant() {
        return assistant;
    }

    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
    }

    public List<TimeSpan> getAvailableTimeSpanList() {
        return availableTimeSpanList;
    }

    public void setAvailableTimeSpanList(List<TimeSpan> availableTimeSpanList) {
        this.availableTimeSpanList = availableTimeSpanList;
    }
}
