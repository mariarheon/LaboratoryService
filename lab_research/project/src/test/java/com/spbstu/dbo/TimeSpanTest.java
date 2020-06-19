package com.spbstu.dbo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TimeSpanTest {
    @Test
    public void testMerging() {
        List<TimeSpan> list = new ArrayList<>();
        list.add(new TimeSpan(10, 0, 12, 0));
        list.add(new TimeSpan(10, 0, 12, 0));
        list.add(new TimeSpan(13, 0, 14, 0));
        list.add(new TimeSpan(12, 30, 12, 40));
        list.add(new TimeSpan(12, 0, 12, 30));
        list.add(new TimeSpan(12, 40, 13, 00));
        List<TimeSpan> res = TimeSpan.mergeAll(list);
        assertEquals("mergeAll not work", res.size(), 1);
        TimeSpan ts = res.get(0);
        assertEquals("mergeAll not work", ts.getStartTime().toString(), "10:00");
        assertEquals("mergeAll not work", ts.getEndTime().toString(), "14:00");
    }
}
