/**
 * Created by mary.mikhaleva on 01.05.20.
 */
package com.spbstu.storage;

import com.spbstu.dbo.*;
import com.spbstu.exceptions.AuthException;
import com.spbstu.exceptions.UserAlreadyExistsException;
import com.spbstu.exceptions.UserNotFoundException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class StorageRepository {

    private UserMapper userMapper;
    private RequestMapper requestMapper;
    private FormMapper formMapper;
    private ScheduleMapper scheduleMapper;

    private static StorageRepository inst;

    private StorageRepository() {
        try {
            userMapper = new UserMapper();
            requestMapper = new RequestMapper();
            formMapper = new FormMapper();
            scheduleMapper = new ScheduleMapper();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static StorageRepository getInstance() {
        if (inst == null) {
            inst = new StorageRepository();
        }
        return inst;
    }

    public void addUser(User user, String password)
            throws UserAlreadyExistsException, SQLException {
        if (userMapper.findByLogin(user.getLogin()) != null)
            throw new UserAlreadyExistsException(user.getLogin());
        userMapper.add(user, password);
    }

    public List<User> getUsersByRole(Role role) throws SQLException {
        return userMapper.findByRole(role);
    }

    public User getUser(String login) throws UserNotFoundException, SQLException {
        User user = userMapper.findByLogin(login);
        if (user == null) throw new UserNotFoundException(login);
        return user;
    }

    public User authenticateUser(String login, String password) throws SQLException, AuthException {
        try {
            User user = getUser(login);
            if (userMapper.authenticateUser(user, password)) {
                return user;
            }
        } catch (UserNotFoundException e) {
            throw new AuthException();
        }
        throw new AuthException();
    }

    public List<Request> getRequestsForClient(User user) throws SQLException {
        return requestMapper.findByUser(user);
    }

    public List<Request> getRequests() throws SQLException {
        return requestMapper.findAll();
    }

    public void addRequest(Request request) throws SQLException {
        requestMapper.add(request);
    }

    public void updateRequest(Request request) throws SQLException {
        requestMapper.update(request);
    }

    public void addForm(Form form) throws SQLException {
        formMapper.add(form);
    }

    public List<Form> getFormsForAssistant(int assistantId) throws SQLException {
        return formMapper.findForAssistant(assistantId);
    }

    public List<Form> getFormsByRequest(Request request) throws SQLException {
        return formMapper.findByRequest(request);
    }

    public void updateForm(Form form) throws SQLException {
        formMapper.update(form);
    }

    public List<String> getAnalysisList() throws SQLException {
        return requestMapper.findAllAnalysis();
    }

    public int getRequiredMinutesForCollection(String analysis) throws SQLException {
        return scheduleMapper.getRequiredMinutesForCollection(analysis);
    }

    public int getRequiredMinutesForResearch(String analysis) throws SQLException {
        return scheduleMapper.getRequiredMinutesForResearch(analysis);
    }

    public int getRequiredMinutesForCollection(Request request) throws SQLException {
        return scheduleMapper.getRequiredMinutesForCollection(request);
    }

    public List<TimeSpan> getAssistantSchedule(User assistant, Weekday weekday) throws SQLException {
        return scheduleMapper.getAssistantSchedule(assistant, weekday);
    }

    public List<TimeSpan> getBusyForAssistantByDate(User assistant, Date date) throws SQLException {
        return scheduleMapper.getBusyForAssistantByDate(assistant, date);
    }

    /*
    synchronized public void drop() throws DBConnectionException {
        try {
            DataGateway.getInstance().dropAll();
        } catch (SQLException e) {
            throw new DBConnectionException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
