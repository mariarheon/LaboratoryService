/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.facade;

import com.spbstu.dbo.*;

import java.util.List;

public interface Facade {
    void register(User user, String password) throws Exception;
    void auth(String login, String password) throws Exception;
    List<Request> getRequestsForClient() throws Exception;
    List<Request> getRequests() throws Exception;
    List<Assistant> getAssistants() throws Exception;
    void logout();
    void addRequest(Request request) throws Exception;
    void editRequest(Request request) throws Exception;
    User getCurrentUser();
    List<Form> getForms() throws Exception;
    void updateForm(Form form) throws Exception;
    void finishFormWork(Form form) throws Exception;
    List<Form> getFormsByRequest(Request request) throws Exception;
    List<String> getAnalysisList() throws Exception;
    List<TimeSpan> getTimeForAppointment(Request request) throws Exception;
    void sendToRandomAssistant(Request request, Time time) throws Exception;
    void checkRequest(Request request) throws Exception;
    void cancelRequest(Request request) throws Exception;
    String getProtocol(String analysis) throws Exception;
}
