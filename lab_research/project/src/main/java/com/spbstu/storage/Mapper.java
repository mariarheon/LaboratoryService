package com.spbstu.storage;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mary.mikhaleva on 01.05.20.
 */
public interface Mapper<T> {
    T findByID(int id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T item) throws SQLException;
    void update() throws SQLException;
    void closeConnection() throws SQLException;
    void clear();
}

