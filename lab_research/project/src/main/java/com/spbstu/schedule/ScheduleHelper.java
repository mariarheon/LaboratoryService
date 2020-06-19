package com.spbstu.schedule;

import com.spbstu.dbo.*;
import com.spbstu.storage.StorageRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    @SuppressWarnings("unchecked")
    private List<AvailableAssistant> getAvailableStartTimes(int reqMinutes) throws SQLException {
        Date reqDate = getOnlyDate(request.arrivalTime);
        Weekday reqWeekday = getWeekday(reqDate);
        List<Assistant> assistants = (List<Assistant>)(List<?>)repo.getUsersByRole(Role.ASSISTANT);
        List<AvailableAssistant> availableAssistants = new ArrayList<>();
        for (Assistant assistant : assistants) {
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

    private List<TimeSpan> getSchedule(Assistant assistant, Weekday weekday) throws SQLException {
        return repo.getAssistantSchedule(assistant, weekday);
    }

    private List<TimeSpan> getBusyByDate(Assistant assistant, Date date) throws SQLException {
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

    public Assistant chooseRandomAssistant(Time time) throws SQLException {
        int reqMin = getRequiredMinutesForCollection(request);
        List<AvailableAssistant> availableList = getAvailableStartTimes(reqMin);
        List<Assistant> suitableAssistants = getSuitableAssistants(availableList, time);
        if (suitableAssistants.size() <= 0) {
            return null;
        }
        int randIndex = ThreadLocalRandom.current().nextInt(suitableAssistants.size());
        return suitableAssistants.get(randIndex);
    }

    private List<Assistant> getSuitableAssistants(List<AvailableAssistant> availableList, Time time) {
        List<Assistant> res = new ArrayList<>();
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
    public int setCollectionTimes(Form form, int minOffset) throws SQLException {
        String analysis = form.getAnalysis();
        int collectionReqMin = repo.getRequiredMinutesForCollection(analysis);
        Calendar c = Calendar.getInstance();
        c.setTime(request.getArrivalTime());
        c.add(Calendar.MINUTE, minOffset);
        form.setCollectionStartDateTime(c.getTime());
        c.add(Calendar.MINUTE, collectionReqMin);
        form.setCollectionEndDateTime(c.getTime());
        return collectionReqMin;
    }

    public void setResearchTimes(Form form) throws SQLException {
        String analysis = form.getAnalysis();
        int researchReqMin = repo.getRequiredMinutesForResearch(analysis);
        Date researchStartTime = chooseResearchStartTime(form, researchReqMin);
        form.setResearchStartDateTime(researchStartTime);
        Calendar c = Calendar.getInstance();
        c.setTime(researchStartTime);
        c.add(Calendar.MINUTE, researchReqMin);
        form.setResearchEndDateTime(c.getTime());
    }

    private Date chooseResearchStartTime(Form form, int researchReqMin) throws SQLException {
        Date collectionEnd = form.getCollectionEndDateTime();
        Date curDate = getOnlyDate(collectionEnd);
        Calendar c = Calendar.getInstance();
        Assistant assistant = form.getAssistant();
        boolean isFirstDate = true;
        while (true) {
            List<TimeSpan> availableTimes = getAvailableStartTimes(curDate, assistant, researchReqMin);
            for (TimeSpan ts : availableTimes) {
                c.setTime(curDate);
                c.set(Calendar.HOUR_OF_DAY, ts.getStartTime().getHour());
                c.set(Calendar.MINUTE, ts.getStartTime().getMinute());
                Date tmp = c.getTime();
                if (ts.getDurationAsMinutes() < researchReqMin) {
                    continue;
                }
                if (isFirstDate) {
                    if (tmp.before(collectionEnd)) {
                        continue;
                    }
                }
                return tmp;
            }
            c.setTime(curDate);
            c.add(Calendar.DATE, 1);
            curDate = c.getTime();
            isFirstDate = false;
        }
    }

    private List<TimeSpan> getAvailableStartTimes(Date date, Assistant assistant, int reqMinutes) throws SQLException {
        Weekday reqWeekday = getWeekday(date);
        List<TimeSpan> schedule = getSchedule(assistant, reqWeekday);
        List<TimeSpan> busy = getBusyByDate(assistant, date);
        List<TimeSpan> free = getFreeTime(schedule, busy);
        List<TimeSpan> startTimeList = getStartTime(free, reqMinutes);
        return startTimeList;
    }
}
