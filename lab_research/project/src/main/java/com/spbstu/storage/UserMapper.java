/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.storage;

import com.spbstu.dbo.Role;
import com.spbstu.dbo.Sex;
import com.spbstu.dbo.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class UserMapper {
    private Connection connection;

    public UserMapper() throws SQLException, IOException {
        connection = DataGateway.getInstance().getConnection();
    }

    public void add(User user, String password) throws SQLException {
        String query = "insert into `user`(login, surname, name,\n" +
            "patronymic, sex, passport_series, passport_number,\n" +
            "role, phone, password) VALUES\n" +
            "(?, ?, ?, ?, ?, ?, ?, ?, ?, SHA1(?));";
        PreparedStatement stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stat.setString(1, user.getLogin());
        stat.setString(2, user.getSurname());
        stat.setString(3, user.getName());
        stat.setString(4, user.getPatronymic());
        stat.setString(5, user.getSex().toString());
        stat.setInt(6, user.getPassportSeries());
        stat.setInt(7, user.getPassportNumber());
        stat.setString(8, user.getRole().toString());
        stat.setString(9, user.getPhone());
        stat.setString(10, password);
        stat.execute();
        ResultSet rs = stat.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            user.setId(id);
        }
    }

    public boolean authenticateUser(User user, String password) throws SQLException {
        String query = "select password from `user` where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, user.getId());
        ResultSet rs = stat.executeQuery();

        if (!rs.next()) return false;

        return encryptPassword(password).equals(rs.getString("password"));
    }

    public List<User> findByRole(Role role) throws SQLException {
        String query = "select id, login, surname, name, patronymic, sex, passport_series, passport_number, role, phone from `user` where role = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, role.toString());
        ResultSet rs = stat.executeQuery();

        List<User> res = new ArrayList<User>();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setPhone(rs.getString("phone"));
            user.setRole(Role.getByStr(rs.getString("role")));
            user.setLogin(rs.getString("login"));
            user.setSurname(rs.getString("surname"));
            user.setName(rs.getString("name"));
            user.setPatronymic(rs.getString("patronymic"));
            user.setSex(Sex.getByStr(rs.getString("sex")));
            user.setPassportSeries(rs.getInt("passport_series"));
            user.setPassportNumber(rs.getInt("passport_number"));
            res.add(user);
        }
        return res;
    }

    public User findByLogin(String login) throws SQLException {
        String query = "select id, login, surname, name, patronymic, sex, passport_series, passport_number, role, phone from `user` where login = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, login);
        ResultSet rs = stat.executeQuery();

        if (!rs.next()) return null;
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setPhone(rs.getString("phone"));
        user.setRole(Role.getByStr(rs.getString("role")));
        user.setLogin(login);
        user.setSurname(rs.getString("surname"));
        user.setName(rs.getString("name"));
        user.setPatronymic(rs.getString("patronymic"));
        user.setSex(Sex.getByStr(rs.getString("sex")));
        user.setPassportSeries(rs.getInt("passport_series"));
        user.setPassportNumber(rs.getInt("passport_number"));
        return user;
    }

    public User findByID(int id) throws SQLException {
        String query = "select id, login, surname, name, patronymic, sex, passport_series, passport_number, role, phone from `user` where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, id);
        ResultSet rs = stat.executeQuery();

        if (!rs.next()) return null;
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setPhone(rs.getString("phone"));
        user.setRole(Role.getByStr(rs.getString("role")));
        user.setLogin(rs.getString("login"));
        user.setSurname(rs.getString("surname"));
        user.setName(rs.getString("name"));
        user.setPatronymic(rs.getString("patronymic"));
        user.setSex(Sex.getByStr(rs.getString("sex")));
        user.setPassportSeries(rs.getInt("passport_series"));
        user.setPassportNumber(rs.getInt("passport_number"));
        return user;
    }

    private static String encryptPassword(String password) {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes(StandardCharsets.UTF_8));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
