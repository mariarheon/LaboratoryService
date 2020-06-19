package com.spbstu.storage;

import com.spbstu.dbo.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RequestMapper {

    private Connection connection;
    private UserMapper userMapper;

    public RequestMapper() throws SQLException, IOException {
        connection = DataGateway.getInstance().getConnection();
        userMapper = new UserMapper();
    }

    public void add(Request request) throws SQLException {
        String query = "insert into request (surname, name, patronymic, sex, passport_series,\n" +
                "    passport_number, arrival_time, status, client_id) values\n" +
                "(?, ?, ?, ?, ?, ?, ?,\n" +
                "?,\n" +
                "?);";
        PreparedStatement stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stat.setString(1, request.getSurname());
        stat.setString(2, request.getName());
        stat.setString(3, request.getPatronymic());
        stat.setString(4, request.getSex().toString());
        stat.setInt(5, request.getPassportSeries());
        stat.setInt(6, request.getPassportNumber());
        stat.setTimestamp(7,  new java.sql.Timestamp(request.getArrivalTime().getTime()));
        stat.setString(8, request.getStatus().toString());
        stat.setInt(9, request.getClient().getId());
        stat.execute();
        ResultSet rs = stat.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            request.setId(id);
        }
        for (String analysis : request.getAnalysisList()) {
            addAnalysisToRequest(analysis, request);
        }
    }

    public void update(Request request) throws SQLException {
        removeAnalysis(request);
        String query = "update request set\n" +
                "surname = ?,\n" +
                "name = ?,\n" +
                "patronymic = ?,\n" +
                "sex = ?,\n" +
                "passport_series = ?,\n" +
                "passport_number = ?,\n" +
                "arrival_time = ?,\n" +
                "status = ?,\n" +
                "client_id = ?\n" +
                "where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        stat.setString(1, request.getSurname());
        stat.setString(2, request.getName());
        stat.setString(3, request.getPatronymic());
        stat.setString(4, request.getSex().toString());
        stat.setInt(5, request.getPassportSeries());
        stat.setInt(6, request.getPassportNumber());
        stat.setTimestamp(7,  new java.sql.Timestamp(request.getArrivalTime().getTime()));
        stat.setString(8, request.getStatus().toString());
        stat.setInt(9, request.getClient().getId());
        stat.setInt(10, request.getId());
        stat.execute();
        for (String analysis : request.getAnalysisList()) {
            addAnalysisToRequest(analysis, request);
        }
    }

    private void removeAnalysis(Request request) throws SQLException {
        String query = "delete from request_analysis_link\n" +
                "where request_id = ?;";
        PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        stat.setInt(1, request.getId());
        stat.execute();
    }

    private void addAnalysisToRequest(String analysis, Request request) throws SQLException {
        String query = "insert into request_analysis_link (request_id, analysis_id) values (\n" +
                "?,\n" +
                "(select id from analysis where name = ?)\n" +
                ");";
        PreparedStatement stat = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        stat.setInt(1, request.getId());
        stat.setString(2, analysis);
        stat.execute();
    }

    public List<Request> findByClient(Client client) throws SQLException {
        String query = "select id, surname, name, patronymic, sex, " +
                " passport_series, passport_number, arrival_time, client_id, " +
                " status " +
                " from request where client_id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, client.getId());
        ResultSet rs = stat.executeQuery();
        List<Request> res = new ArrayList<Request>();
        while (rs.next()) {
            Request request = new Request();
            request.setId(rs.getInt("id"));
            request.setSurname(rs.getString("surname"));
            request.setName(rs.getString("name"));
            request.setPatronymic(rs.getString("patronymic"));
            request.setSex(Sex.getByStr(rs.getString("sex")));
            request.setPassportSeries(rs.getInt("passport_series"));
            request.setPassportNumber(rs.getInt("passport_number"));
            request.setStatus(RequestStatus.getByStr(rs.getString("status")));
            request.setArrivalTime(new java.util.Date(rs.getTimestamp("arrival_time").getTime()));
            request.setClient(client);
            res.add(request);
        }
        rs.close();
        for (Request r : res) {
            r.setAnalysisList(findAnalysis(r.getId()));
        }
        return res;
    }

    public Request findById(int requestId) throws SQLException {
        String query = "select id, surname, name, patronymic, sex, " +
                " passport_series, passport_number, arrival_time, client_id, " +
                " status " +
                " from request where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, requestId);
        ResultSet rs = stat.executeQuery();
        Request request = null;
        if (rs.next()) {
            request = new Request();
            request.setId(rs.getInt("id"));
            request.setSurname(rs.getString("surname"));
            request.setName(rs.getString("name"));
            request.setPatronymic(rs.getString("patronymic"));
            request.setSex(Sex.getByStr(rs.getString("sex")));
            request.setPassportSeries(rs.getInt("passport_series"));
            request.setPassportNumber(rs.getInt("passport_number"));
            request.setStatus(RequestStatus.getByStr(rs.getString("status")));
            request.setArrivalTime(new java.util.Date(rs.getTimestamp("arrival_time").getTime()));
            User client = userMapper.findByID(rs.getInt("client_id"));
            if (client instanceof Client) {
                request.setClient((Client)client);
            } else {
                throw new RuntimeException("ID of client expected, but ID of another type of user found");
            }
        }
        rs.close();
        request.setAnalysisList(findAnalysis(request.getId()));
        return request;
    }

    public List<Request> findAll() throws SQLException {
        String query = "select id, surname, name, patronymic, sex, " +
                " passport_series, passport_number, arrival_time, client_id, " +
                " status " +
                " from request;";
        PreparedStatement stat = connection.prepareStatement(query);
        ResultSet rs = stat.executeQuery();
        List<Request> res = new ArrayList<Request>();
        while (rs.next()) {
            Request request = new Request();
            request.setId(rs.getInt("id"));
            request.setSurname(rs.getString("surname"));
            request.setName(rs.getString("name"));
            request.setPatronymic(rs.getString("patronymic"));
            request.setSex(Sex.getByStr(rs.getString("sex")));
            request.setPassportSeries(rs.getInt("passport_series"));
            request.setPassportNumber(rs.getInt("passport_number"));
            request.setStatus(RequestStatus.getByStr(rs.getString("status")));
            request.setArrivalTime(new java.util.Date(rs.getTimestamp("arrival_time").getTime()));
            User client = userMapper.findByID(rs.getInt("client_id"));
            if (client instanceof Client) {
                request.setClient((Client)client);
            } else {
                throw new RuntimeException("ID of client expected, but ID of another type of user found");
            }
            res.add(request);
        }
        rs.close();
        for (Request r : res) {
            r.setAnalysisList(findAnalysis(r.getId()));
        }
        return res;
    }

    public List<String> findAllAnalysis() throws SQLException {
        String query = "select name\n" +
                "from analysis;";
        PreparedStatement stat = connection.prepareStatement(query);
        ResultSet rs = stat.executeQuery();
        List<String> res = new ArrayList<String>();
        while (rs.next()) {
            res.add(rs.getString("name"));
        }
        return res;
    }

    private List<String> findAnalysis(int requestId) throws SQLException {
        String query = "select a.name\n" +
                "from request_analysis_link l\n" +
                "left join analysis a on l.analysis_id = a.id\n" +
                "where request_id = ?;\n";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, requestId);
        ResultSet rs = stat.executeQuery();
        List<String> res = new ArrayList<String>();
        while (rs.next()) {
            res.add(rs.getString("name"));
        }
        return res;
    }

    public String findAnalysisById(int analysisId) throws SQLException {
        String query = "select name\n" +
                "from analysis\n" +
                "where id = ?;\n";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, analysisId);
        ResultSet rs = stat.executeQuery();
        String res = "";
        if (rs.next()) {
            res = rs.getString("name");
        }
        return res;
    }
}
