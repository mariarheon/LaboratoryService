package com.spbstu.dbo;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class TimeSpan {
    private Time startTime;
    private Time endTime;

    public TimeSpan() {
        startTime = new Time();
        endTime = new Time();
    }

    public TimeSpan(TimeSpan other) {
        startTime = new Time(other.getStartTime());
        endTime = new Time(other.getEndTime());
    }

    public TimeSpan(Time start, Time end) {
        this.startTime = start;
        this.endTime = end;
    }

    public TimeSpan(java.util.Date start, java.util.Date end) {
        this.startTime = new Time(start);
        this.endTime = new Time(end);
    }

    public TimeSpan(int hour1, int min1, int hour2, int min2) {
        this.startTime = new Time(hour1, min1);
        this.endTime = new Time(hour2, min2);
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public boolean isBad() {
        return startTime.getTotalMinutes() >= endTime.getTotalMinutes();
    }

    public List<TimeSpan> subtract(TimeSpan other) {
        List<TimeSpan> res = new ArrayList<>();
        res.add(new TimeSpan(this));
        if (isBad() || other.isBad()) {
            return res;
        }
        boolean notIntersects = other.startTime.afterOrEqual(endTime)
                || other.endTime.beforeOrEqual(startTime);
        if (notIntersects) {
            return res;
        }
        res.clear();
        if (other.startTime.beforeOrEqual(startTime) && other.endTime.before(endTime)) {
            res.add(new TimeSpan(other.endTime, endTime));
            return res;
        }
        if (other.startTime.beforeOrEqual(startTime) && other.endTime.afterOrEqual(endTime)) {
            return res;
        }
        if (other.startTime.after(startTime) && other.endTime.afterOrEqual(endTime)) {
            res.add(new TimeSpan(startTime, other.startTime));
            return res;
        }
        // other.startTime.after(startTime) && other.endTime.before(endTime)
        res.add(new TimeSpan(startTime, other.startTime));
        res.add(new TimeSpan(other.endTime, endTime));
        return res;
    }

    public static List<TimeSpan> subtractAll(List<TimeSpan> fromList, List<TimeSpan> whatList) {
        List<TimeSpan> res = fromList;
        for (TimeSpan what : whatList) {
            res = subtractAll(res, what);
        }
        return res;
    }

    private static List<TimeSpan> subtractAll(List<TimeSpan> fromList, TimeSpan what) {
        List<TimeSpan> res = new ArrayList<>();
        for (TimeSpan from : fromList) {
            res.addAll(from.subtract(what));
        }
        return res;
    }

    public TimeSpan reduceEnd(int minutes) {
        int newEndTime = endTime.getTotalMinutes() - minutes;
        TimeSpan res = new TimeSpan(startTime, new Time(newEndTime));
        if (res.isBad()) {
            return null;
        }
        return res;
    }

    public static List<TimeSpan> mergeAll(List<TimeSpan> timeSpanList) {
        // Test if the given set has at least one interval
        if (timeSpanList.size() <= 0) {
            return new ArrayList<>();
        }

        // Create an empty stack of intervals
        Stack<TimeSpan> stack=new Stack<>();

        // sort the intervals in increasing order of start time
        List<TimeSpan> sortedResult = timeSpanList.stream()
                .sorted(Comparator.comparingInt(
                        timeSpan -> timeSpan.getStartTime().getTotalMinutes()))
                .collect(Collectors.toList());

        // push the first interval to stack
        stack.push(sortedResult.get(0));

        // Start from the next interval and merge if necessary
        for (int i = 1; i < sortedResult.size(); i++)
        {
            // get interval from stack top
            TimeSpan top = stack.peek();

            if (top.endTime.before(sortedResult.get(i).startTime)) {
                // if current interval is not overlapping with stack top,
                // push it to the stack
                stack.push(sortedResult.get(i));
            }  else if (top.endTime.before(sortedResult.get(i).endTime)) {
                // Otherwise update the ending time of top if ending of current
                // interval is more
                top = new TimeSpan(top.startTime, sortedResult.get(i).endTime);
                stack.pop();
                stack.push(top);
            }
        }

        // Print contents of stack
        List<TimeSpan> res = new LinkedList<>();
        while (!stack.isEmpty())
        {
            TimeSpan t = stack.pop();
            res.add(0, t);
        }
        return res;
    }

    public boolean contains(Time time) {
        if (isBad()) {
            return false;
        }
        return time.afterOrEqual(startTime) && time.beforeOrEqual(endTime);
    }

    public int getDurationAsMinutes() {
        return endTime.getTotalMinutes() - startTime.getTotalMinutes();
    }

    /*
    public static void main(String[] args) {
        List<TimeSpan> list = new ArrayList<>();
        list.add(new TimeSpan(10, 0, 12, 0));
        list.add(new TimeSpan(10, 0, 12, 0));
        list.add(new TimeSpan(13, 0, 14, 0));
        list.add(new TimeSpan(12, 30, 12, 40));
        list.add(new TimeSpan(12, 0, 12, 30));
        list.add(new TimeSpan(12, 40, 13, 00));
        List<TimeSpan> res = mergeAll(list);
        for (TimeSpan ts : res) {
            System.out.println(ts.startTime.toString() + ", " + ts.endTime.toString());
        }
    }
    */
}
