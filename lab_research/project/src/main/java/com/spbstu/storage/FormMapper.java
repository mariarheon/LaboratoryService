package com.spbstu.storage;

import com.spbstu.dbo.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class FormMapper {
    private Connection connection;
    private RequestMapper requestMapper;
    private UserMapper userMapper;

    public FormMapper() throws SQLException, IOException {
        connection = DataGateway.getInstance().getConnection();
        requestMapper = new RequestMapper();
        userMapper = new UserMapper();
    }

    public void add(Form form) throws SQLException {
        addForm(form);
        addFields(form);
        addBusyTimes(form);
    }

    public void update(Form form) throws SQLException {
        removeBusyTimes(form);
        removeFields(form);
        editForm(form);
        addFieldsWithValue(form);
        addBusyTimes(form);
    }

    private void removeBusyTimes(Form form) throws SQLException {
        String query = "delete from `busy`\n" +
                " where form_id = ?;";
        PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        stat.setInt(1, form.getId());
        stat.execute();
    }

    private void removeFields(Form form) throws SQLException {
        String query = "delete from `form_field_link`\n" +
                " where form_id = ?;";
        PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        stat.setInt(1, form.getId());
        stat.execute();
    }

    private void editForm(Form form) throws SQLException {
        String query = "update `form` set\n" +
                "request_id = ?,\n" +
                "analysis_id = (select id from analysis where `name` = ?),\n" +
                "assistant_id = ?,\n" +
                "barcode = ?,\n" +
                "status = ?\n" +
                "where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        stat.setInt(1, form.getRequest().getId());
        stat.setString(2, form.getAnalysis());
        stat.setInt(3, form.getAssistant().getId());
        stat.setString(4, form.getBarcode());
        stat.setString(5, form.getStatus().toString());
        stat.setInt(6, form.getId());
        stat.execute();
    }

    private void addFieldsWithValue(Form form) throws SQLException {
        List<FormField> formFieldList = form.getFields();
        if (formFieldList == null) {
            return;
        }
        for (FormField formField : formFieldList) {
            String query = "insert into `form_field_link` (form_id, field_id, value) values\n" +
                    "(?, ?, ?);";
            PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
            stat.setInt(1, form.getId());
            stat.setInt(2, formField.getId());
            stat.setString(3, formField.getValue());
            stat.execute();
        }
    }

    private void addBusyTimes(Form form) throws SQLException {
        java.util.Date collectionStart = form.getCollectionStartDateTime();
        java.util.Date collectionEnd = form.getCollectionEndDateTime();
        java.util.Date researchStart = form.getResearchStartDateTime();
        java.util.Date researchEnd = form.getResearchEndDateTime();
        if (collectionStart != null && collectionEnd != null) {
            String query = "insert into `busy` (assistant_id, the_date, start_time, end_time, form_id, reason)\n" +
                    "values (?, ?, ?, ?, ?, 'сбор');";
            PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
            stat.setInt(1, form.getAssistant().getId());
            stat.setDate(2, new java.sql.Date(collectionStart.getTime()));
            stat.setTime(3, new java.sql.Time(collectionStart.getTime()));
            stat.setTime(4, new java.sql.Time(collectionEnd.getTime()));
            stat.setInt(5, form.getId());
            stat.execute();
        }
        if (researchStart != null && researchEnd != null) {
            String query = "insert into `busy` (assistant_id, the_date, start_time, end_time, form_id, reason)\n" +
                    "values (?, ?, ?, ?, ?, 'исследование');";
            PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
            stat.setInt(1, form.getAssistant().getId());
            stat.setDate(2, new java.sql.Date(researchStart.getTime()));
            stat.setTime(3, new java.sql.Time(researchStart.getTime()));
            stat.setTime(4, new java.sql.Time(researchEnd.getTime()));
            stat.setInt(5, form.getId());
            stat.execute();
        }
    }

    private void addForm(Form form) throws SQLException {
        String query = "insert into `form` (request_id, analysis_id, assistant_id, barcode)\n" +
                "values (?, (select id from `analysis` where `name` = ?), ?, ?);";
        PreparedStatement stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stat.setInt(1, form.getRequest().getId());
        stat.setString(2, form.getAnalysis());
        stat.setInt(3, form.getAssistant().getId());
        stat.setString(4, form.getBarcode());
        stat.execute();
        ResultSet rs = stat.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            form.setId(id);
        }
    }

    private void addFields(Form form) throws SQLException {
        String query = "insert into `form_field_link` (form_id, field_id, `value`)\n" +
                "select ?, id, ''\n" +
                "from form_field\n" +
                "where analysis_id = (select id from `analysis` where `name` = ?)\n" +
                "order by order_num;";
        PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        stat.setInt(1, form.getId());
        stat.setString(2, form.getAnalysis());
        stat.execute();
    }

    public List<Form> findForAssistant(int assistantId) throws SQLException {
        String query = "select id, request_id, analysis_id, assistant_id, barcode, `status`\n" +
                "from form\n" +
                "where assistant_id = ? and `status` = 'В работе';";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, assistantId);
        ResultSet rs = stat.executeQuery();
        List<Form> res = new ArrayList<Form>();
        while (rs.next()) {
            Form form = new Form();
            form.setId(rs.getInt("id"));
            form.setBarcode(rs.getString("barcode"));
            form.setStatus(FormStatus.getByStr(rs.getString("status")));

            Request request = requestMapper.findById(rs.getInt("request_id"));
            form.setRequest(request);

            User assistant = userMapper.findByID(rs.getInt("assistant_id"));
            form.setAssistant(assistant);

            String analysis = requestMapper.findAnalysisById(rs.getInt("analysis_id"));
            form.setAnalysis(analysis);

            res.add(form);
        }
        rs.close();
        for (Form f : res) {
            f.setFields(findFields(f.getId()));
            fillWithTimes(f);
        }
        return res;
    }

    private void fillWithTimes(Form form) throws SQLException {
        fillWithCollectionTimes(form);
        fillWithResearchTimes(form);
    }

    private void fillWithCollectionTimes(Form form) throws SQLException {
        String query = "select the_date, start_time, end_time\n" +
                "from busy\n" +
                "where form_id = ? and reason = 'сбор';";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, form.getId());
        ResultSet rs = stat.executeQuery();
        Calendar c = Calendar.getInstance();
        if (rs.next()) {
            java.sql.Date date = rs.getDate("the_date");
            java.sql.Time startTime = rs.getTime("start_time");
            java.sql.Time endTime = rs.getTime("end_time");
            java.util.Date collectionStart = mergeDateAndTime(date, startTime);
            java.util.Date collectionEnd = mergeDateAndTime(date, endTime);
            form.setCollectionStartDateTime(collectionStart);
            form.setCollectionEndDateTime(collectionEnd);
        }
        rs.close();
    }

    private void fillWithResearchTimes(Form form) throws SQLException {
        String query = "select the_date, start_time, end_time\n" +
                "from busy\n" +
                "where form_id = ? and reason = 'исследование';";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, form.getId());
        ResultSet rs = stat.executeQuery();
        Calendar c = Calendar.getInstance();
        if (rs.next()) {
            java.sql.Date date = rs.getDate("the_date");
            java.sql.Time startTime = rs.getTime("start_time");
            java.sql.Time endTime = rs.getTime("end_time");
            java.util.Date researchStart = mergeDateAndTime(date, startTime);
            java.util.Date researchEnd = mergeDateAndTime(date, endTime);
            form.setResearchStartDateTime(researchStart);
            form.setResearchEndDateTime(researchEnd);
        }
        rs.close();
    }

    private java.util.Date mergeDateAndTime(java.sql.Date date, java.sql.Time time) {
        // Construct date and time objects
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        // Extract the time of the "time" object to the "date"
        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));

        // Get the time value!
        return dateCal.getTime();
    }

    public List<Form> findByRequest(Request requestArg) throws SQLException {
        String query = "select id, request_id, analysis_id, assistant_id, barcode, `status`\n" +
                "from form\n" +
                "where request_id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, requestArg.getId());
        ResultSet rs = stat.executeQuery();
        List<Form> res = new ArrayList<Form>();
        while (rs.next()) {
            Form form = new Form();
            form.setId(rs.getInt("id"));
            form.setBarcode(rs.getString("barcode"));
            form.setStatus(FormStatus.getByStr(rs.getString("status")));

            Request request = requestMapper.findById(rs.getInt("request_id"));
            form.setRequest(request);

            User assistant = userMapper.findByID(rs.getInt("assistant_id"));
            form.setAssistant(assistant);

            String analysis = requestMapper.findAnalysisById(rs.getInt("analysis_id"));
            form.setAnalysis(analysis);

            res.add(form);
        }
        rs.close();
        for (Form f : res) {
            f.setFields(findFields(f.getId()));
            fillWithTimes(f);
        }
        return res;
    }

    private List<FormField> findFields(int formId) throws SQLException {
        String query = "select ff.id, ff.name, ff.description, coalesce(ffl.value, '') value, ff.units\n" +
                "from form_field ff\n" +
                "left join (select * from form_field_link where form_id = ?) ffl on ff.id = ffl.field_id\n" +
                "where ff.analysis_id = (select analysis_id from form where id = ?)\n" +
                "order by ff.order_num;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, formId);
        stat.setInt(2, formId);
        ResultSet rs = stat.executeQuery();
        List<FormField> res = new ArrayList<FormField>();
        while (rs.next()) {
            FormField formField = new FormField();
            formField.setId(rs.getInt("id"));
            formField.setName(rs.getString("name"));
            formField.setDescription(rs.getString("description"));
            formField.setValue(rs.getString("value"));
            formField.setUnits(rs.getString("units"));
            res.add(formField);
        }
        rs.close();
        return res;
    }
}
