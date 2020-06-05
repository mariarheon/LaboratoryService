package com.spbstu.storage;

import com.spbstu.dbo.Request;
import com.spbstu.dbo.TimeSpan;
import com.spbstu.dbo.User;
import com.spbstu.dbo.Weekday;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class ScheduleMapper {
    private Connection connection;

    public ScheduleMapper() throws SQLException, IOException {
        connection = DataGateway.getInstance().getConnection();
    }

    public int getRequiredMinutesForCollection(Request request) throws SQLException {
        String query = "with\n" +
                "analysis_list as (select analysis_id\n" +
                "from request_analysis_link\n" +
                "where request_id = ?)\n" +
                "select sum(collection_minutes) collection_total_minutes\n" +
                "from analysis where id in (select analysis_id from analysis_list);";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, request.getId());
        ResultSet rs = stat.executeQuery();
        int res = 1;
        if (rs.next()) {
            res = rs.getInt("collection_total_minutes");
        }
        return res;
    }

    public int getRequiredMinutesForCollection(String analysis) throws SQLException {
        String query = "select collection_minutes\n" +
                "from analysis where id = (select id from analysis where name = ?);";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, analysis);
        ResultSet rs = stat.executeQuery();
        int res = 1;
        if (rs.next()) {
            res = rs.getInt("collection_minutes");
        }
        return res;
    }

    public int getRequiredMinutesForResearch(String analysis) throws SQLException {
        String query = "select research_minutes\n" +
                "from analysis where id = (select id from analysis where name = ?);";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, analysis);
        ResultSet rs = stat.executeQuery();
        int res = 1;
        if (rs.next()) {
            res = rs.getInt("research_minutes");
        }
        return res;
    }

    public List<TimeSpan> getAssistantSchedule(User assistant, Weekday weekday) throws SQLException {
        String query = "select start_time, end_time\n" +
                "from schedule\n" +
                "where assistant_id = ? and weekday = ? order by start_time;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, assistant.getId());
        stat.setString(2, weekday.toString());
        ResultSet rs = stat.executeQuery();
        List<TimeSpan> res = new ArrayList<TimeSpan>();
        while (rs.next()) {
            java.sql.Time startTime = rs.getTime("start_time");
            java.sql.Time endTime = rs.getTime("end_time");
            res.add(new TimeSpan(startTime, endTime));
        }
        return res;
    }

    public List<TimeSpan> getBusyForAssistantByDate(User assistant, Date date) throws SQLException {
        String query = "select start_time, end_time\n" +
                "from busy\n" +
                "where assistant_id = ? and the_date = ? order by start_time;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, assistant.getId());
        stat.setDate(2, new java.sql.Date(date.getTime()));
        ResultSet rs = stat.executeQuery();
        List<TimeSpan> res = new ArrayList<TimeSpan>();
        while (rs.next()) {
            java.sql.Time startTime = rs.getTime("start_time");
            java.sql.Time endTime = rs.getTime("end_time");
            res.add(new TimeSpan(startTime, endTime));
        }
        return res;
    }
}
