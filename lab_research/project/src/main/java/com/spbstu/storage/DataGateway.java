/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.storage;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.spbstu.config.ConfigReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DataGateway {

    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static DataGateway inst;
    private static MysqlDataSource dataSource;

    private DataGateway() throws IOException {
        ConfigReader config = ConfigReader.getInstance();
        dataSource = new MysqlDataSource();
        dataSource.setURL(config.getDburl());
        dataSource.setUser(config.getDbuser());
        dataSource.setPassword(config.getDbpassword());

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException se) {
            se.printStackTrace();
        }
    }

    public static DataGateway getInstance() throws IOException {
        if(inst == null)
            inst = new DataGateway();
        return inst;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void dropAll() throws SQLException {
        // executeSqlScript(getDataSource().getConnection(), new File("db_create.sql"));
    }

    private void executeSqlScript(Connection conn, File inputFile) {

        // Delimiter
        String delimiter = ";";

        // Create scanner
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile).useDelimiter(delimiter);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }

        // Loop through the SQL file statements
        Statement currentStatement = null;
        while(scanner.hasNext()) {

            // Get statement
            String rawStatement = scanner.next() + delimiter;
            try {
                // Execute statement
                currentStatement = conn.createStatement();
                currentStatement.execute(rawStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Release resources
                if (currentStatement != null) {
                    try {
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        scanner.close();
    }

}