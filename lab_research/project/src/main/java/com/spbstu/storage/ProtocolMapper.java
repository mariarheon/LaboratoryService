/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.storage;

import com.spbstu.dbo.Role;
import com.spbstu.dbo.Sex;
import com.spbstu.dbo.User;
import com.spbstu.dbo.UserBase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class ProtocolMapper {
    private Connection connection;

    public ProtocolMapper() throws SQLException, IOException {
        connection = DataGateway.getInstance().getConnection();
    }

    public String find(String analysis) throws SQLException {
        String query = "select `data` from `protocol` where analysis_id = (select id from analysis where name = ?);";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, analysis);
        ResultSet rs = stat.executeQuery();
        if (rs.next()) {
            return rs.getString("data");
        }
        return "";
    }
}
