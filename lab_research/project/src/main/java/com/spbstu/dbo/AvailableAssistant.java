package com.spbstu.dbo;

import java.util.List;

/**
 *
 */
public class AvailableAssistant {
    private User assistant;
    private List<TimeSpan> availableTimeSpanList;

    public AvailableAssistant(User assistant, List<TimeSpan> availableTimeSpanList) {
        this.assistant = assistant;
        this.availableTimeSpanList = availableTimeSpanList;
    }

    public User getAssistant() {
        return assistant;
    }

    public void setAssistant(User assistant) {
        this.assistant = assistant;
    }

    public List<TimeSpan> getAvailableTimeSpanList() {
        return availableTimeSpanList;
    }

    public void setAvailableTimeSpanList(List<TimeSpan> availableTimeSpanList) {
        this.availableTimeSpanList = availableTimeSpanList;
    }
}
