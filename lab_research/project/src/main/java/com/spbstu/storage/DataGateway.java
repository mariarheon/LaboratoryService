/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.storage;

// import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.spbstu.config.ConfigReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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

    public void revive(InputStream reviveScript) throws SQLException {
        executeSqlScript(reviveScript);
    }

    private String readStream(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private List<String> divideToQueries(String statement) {
        boolean quote = false;
        List<String> queries = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < statement.length(); i++) {
            char ch = statement.charAt(i);
            if (ch == '\'') {
                quote = !quote;
            } else if (ch == ';') {
                if (!quote) {
                    builder.append(ch);
                    queries.add(builder.toString());
                    builder = new StringBuilder();
                    continue;
                }
            }
            builder.append(ch);
        }
        queries.add(builder.toString());
        return queries
                .stream()
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private void executeSqlScript(InputStream inputStream) {
        String text = readStream(inputStream);
        List<String> queries = divideToQueries(text);
        Statement stat = null;
        try {
            Connection conn = getConnection();
            for (String query : queries) {
                try {
                    stat = conn.createStatement();
                    stat.execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (stat != null) {
                        try {
                            stat.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    stat = null;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*
        Statement stat = null;
        try {
            // Execute statement
            stat = conn.createStatement();
            stat.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Release resources
            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            stat = null;
        }
        */
        /*
        // Delimiter
        String delimiter = ";";

        // Create scanner
        Scanner scanner;
        scanner = new Scanner(inputStream).useDelimiter(delimiter);

        // Loop through the SQL file statements
        Statement currentStatement = null;
        while(scanner.hasNext()) {
            String theQuery = scanner.next();
            if (theQuery.trim().equals("")) {
                continue;
            }
            // Get statement
            String rawStatement = theQuery + delimiter;

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
        */
    }

}