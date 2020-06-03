/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.facade;

import com.spbstu.dbo.Form;
import com.spbstu.dbo.Request;
import com.spbstu.dbo.Role;
import com.spbstu.dbo.User;

import java.util.Date;
import java.util.List;

public interface Facade {
    void register(User user, String password) throws Exception;
    void auth(String login, String password) throws Exception;
    List<Request> getRequestsForClient() throws Exception;
    List<Request> getRequests() throws Exception;
    List<User> getAssistants() throws Exception;
    void logout();
    void addRequest(Request request) throws Exception;
    void editRequest(Request request) throws Exception;
    User getCurrentUser();
    void addForms(Request request, User assistant) throws Exception;
    List<Form> getForms() throws Exception;
    void updateForm(Form form) throws Exception;
}
