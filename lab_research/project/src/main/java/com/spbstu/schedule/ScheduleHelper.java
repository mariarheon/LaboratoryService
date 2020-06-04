package com.spbstu.schedule;

import com.spbstu.dbo.*;
import com.spbstu.storage.StorageRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 */
public class ScheduleHelper {
    private StorageRepository repo;
    private Request request;

    public ScheduleHelper(StorageRepository repo, Request request) {
        this.repo = repo;
        this.request = request;
    }

    public List<TimeSpan> getAvailableStartTimesForCollection() throws SQLException {
        int reqMin = getRequiredMinutesForCollection(request);
        List<AvailableAssistant> availableList = getAvailableStartTimes(reqMin);
        return getMergedTimeSpanList(availableList);
    }

    public List<AvailableAssistant> getAvailableStartTimesForResearch() throws SQLException {
        int reqMin = 0; // (request);
        return getAvailableStartTimes(reqMin);
    }

    private List<AvailableAssistant> getAvailableStartTimes(int reqMinutes) throws SQLException {
        Date reqDate = getOnlyDate(request.arrivalTime);
        Weekday reqWeekday = getWeekday(reqDate);
        List<User> assistants = repo.getUsersByRole(Role.ASSISTANT);
        List<AvailableAssistant> availableAssistants = new ArrayList<>();
        for (User assistant : assistants) {
            List<TimeSpan> schedule = getSchedule(assistant, reqWeekday);
            List<TimeSpan> busy = getBusyByDate(assistant, reqDate);
            List<TimeSpan> free = getFreeTime(schedule, busy);
            List<TimeSpan> startTimeList = getStartTime(free, reqMinutes);
            availableAssistants.add(new AvailableAssistant(assistant, startTimeList));
        }
        return availableAssistants;
    }

    private List<TimeSpan> getMergedTimeSpanList(List<AvailableAssistant> availableAssistants) {
        List<TimeSpan> result = new ArrayList<>();
        for (AvailableAssistant availableAssistant : availableAssistants) {
            List<TimeSpan> timeSpanList = availableAssistant.getAvailableTimeSpanList();
            result.addAll(timeSpanList);
        }
        return TimeSpan.mergeAll(result);
    }

    private Date getOnlyDate(Date dateTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(request.arrivalTime);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.clear();
        c.set(year, month, day);
        java.util.Date onlyDate = c.getTime();
        return onlyDate;
    }

    // Количество минут, требуемое на сбор биоматериала
    private int getRequiredMinutesForCollection(Request request) throws SQLException {
        return repo.getRequiredMinutesForCollection(request);
    }

    private Weekday getWeekday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekdayNumber = c.get(Calendar.DAY_OF_WEEK);
        return Weekday.getByNumber(weekdayNumber);
    }

    private List<TimeSpan> getSchedule(User assistant, Weekday weekday) throws SQLException {
        return repo.getAssistantSchedule(assistant, weekday);
    }

    private List<TimeSpan> getBusyByDate(User assistant, Date date) throws SQLException {
        return repo.getBusyForAssistantByDate(assistant, date);
    }

    private List<TimeSpan> getFreeTime(List<TimeSpan> schedule, List<TimeSpan> busy) {
        return TimeSpan.subtractAll(schedule, busy);
    }

    // Получает диапазоны времён, которые доступны для времени начала выполнения
    // задачи, которая требует reqMin минут.
    private List<TimeSpan> getStartTime(List<TimeSpan> freeTime, int reqMin) {
        List<TimeSpan> res = new ArrayList<>();
        for (TimeSpan freeTimeItem : freeTime) {
            TimeSpan reduced = freeTimeItem.reduceEnd(reqMin);
            if (reduced != null) {
                res.add(reduced);
            }
        }
        return res;
    }

    public User chooseRandomAssistant(Time time) throws SQLException {
        int reqMin = getRequiredMinutesForCollection(request);
        List<AvailableAssistant> availableList = getAvailableStartTimes(reqMin);
        List<User> suitableAssistants = getSuitableAssistants(availableList, time);
        if (suitableAssistants.size() <= 0) {
            return null;
        }
        int randIndex = ThreadLocalRandom.current().nextInt(suitableAssistants.size());
        return suitableAssistants.get(randIndex);
    }

    private List<User> getSuitableAssistants(List<AvailableAssistant> availableList, Time time) {
        List<User> res = new ArrayList<>();
        for (AvailableAssistant assistant : availableList) {
            if (assistantCanBeUsed(assistant, time)) {
                res.add(assistant.getAssistant());
            }
        }
        return res;
    }

    private boolean assistantCanBeUsed(AvailableAssistant assistant, Time time) {
        List<TimeSpan> timeSpanList = assistant.getAvailableTimeSpanList();
        for (TimeSpan ts : timeSpanList) {
            if (ts.contains(time)) {
                return true;
            }
        }
        return false;
    }

    // returns collection min count for this
    public int setTimes(Form form, int minOffset) throws SQLException {
        String analysis = form.getAnalysis();
        int collectionReqMin = repo.getRequiredMinutesForCollection(analysis);
        int researchReqMin = repo.getRequiredMinutesForResearch(analysis);
        Calendar c = Calendar.getInstance();
        c.setTime(request.getArrivalTime());
        c.add(Calendar.MINUTE, minOffset);
        form.setCollectionStartDateTime(c.getTime());
        c.add(Calendar.MINUTE, collectionReqMin);
        form.setCollectionEndDateTime(c.getTime());
        Date researchStartTime = chooseResearchStartTime(form.getAssistant(), researchReqMin, form);
        form.setResearchStartDateTime(researchStartTime);
        c.setTime(researchStartTime);
        c.add(Calendar.MINUTE, researchReqMin);
        form.setResearchEndDateTime(c.getTime());
        return collectionReqMin;
    }

    private Date chooseResearchStartTime(User assistant, int researchReqMin, Form form) {
        // use additional busy from form
        return null;
    }
}
