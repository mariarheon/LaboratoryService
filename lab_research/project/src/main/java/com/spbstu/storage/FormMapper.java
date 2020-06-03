package com.spbstu.storage;

import com.spbstu.dbo.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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
    }

    public void update(Form form) throws SQLException {
        removeFields(form);
        editForm(form);
        addFieldsWithValue(form);
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
        for (FormField formField : formFieldList) {
            String query = "insert `form_field_link` set\n" +
                    "form_id = ?,\n" +
                    "field_id = ?,\n" +
                    "value = ?;";
            PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
            stat.setInt(1, form.getId());
            stat.setInt(2, formField.getId());
            stat.setString(3, formField.getValue());
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
                "where assistant_id = ?;";
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
        }
        return res;
    }

    private List<FormField> findFields(int formId) throws SQLException {
        String query = "select ff.id, ff.name, ff.description, coalesce(ffl.value, '') value, ff.units\n" +
                "from form_field ff\n" +
                "left join form_field_link ffl on ff.id = ffl.field_id\n" +
                "where ff.analysis_id = (select analysis_id from form where id = ?)\n" +
                "order by ff.order_num;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, formId);
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
