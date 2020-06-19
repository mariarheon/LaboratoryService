/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.facade;

import com.spbstu.dbo.*;
import com.spbstu.exceptions.BadChoosedTimeException;
import com.spbstu.schedule.ScheduleHelper;
import com.spbstu.storage.StorageRepository;
import com.spbstu.util.StringTypeVerifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FacadeImpl implements Facade {
    private StorageRepository repository = StorageRepository.getInstance();
    private User currentUser;

    public FacadeImpl() {
    }

    @Override
    public void register(User user, String password) throws Exception {
        user.validate();
        StringTypeVerifier.notEmptyString(password, "Введите пароль");
        repository.addUser(user, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Assistant> getAssistants() throws Exception {
        return (List<Assistant>)(List<?>)repository.getUsersByRole(Role.ASSISTANT);
    }

    @Override
    public void auth(String login, String password) throws Exception {
        StringTypeVerifier.notEmptyString(login, "Логин должен быть указан");
        StringTypeVerifier.notEmptyString(password, "Пароль должен быть указан");
        currentUser = repository.authenticateUser(login, password);
    }

    @Override
    public List<Request> getRequestsForClient() throws Exception {
        if (currentUser instanceof Client) {
            return repository.getRequestsForClient((Client)currentUser);
        }
        return null;
    }

    @Override
    public List<Request> getRequests() throws Exception {
        return repository.getRequests();
    }

    @Override
    public void logout() {
        currentUser = null;
    }

    @Override
    public void addRequest(Request request) throws Exception {
        checkRequest(request);
        repository.addRequest(request);
    }

    @Override
    public void editRequest(Request request) throws Exception {
        checkRequest(request);
        repository.updateRequest(request);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    private void addForms(Request request, Assistant assistant) throws Exception {
        List<String> analysisList = request.getAnalysisList();
        ScheduleHelper scheduleHelper = new ScheduleHelper(repository, request);
        int minOffset = 0;
        List<Form> forms = new ArrayList<>();
        for (String analysis : analysisList) {
            Form form = new Form();
            form.setRequest(request);
            form.setAnalysis(analysis);
            form.setAssistant(assistant);
            minOffset += scheduleHelper.setCollectionTimes(form, minOffset);
            repository.addForm(form);
            forms.add(form);
        }
        for (Form form : forms) {
            scheduleHelper.setResearchTimes(form);
            repository.updateForm(form);
        }
        request.setStatus(RequestStatus.IN_WORK);
        repository.updateRequest(request);
    }

    @Override
    public List<Form> getForms() throws Exception {
        User user = getCurrentUser();
        if (user == null) {
            return null;
        }
        if (user.getRole() != Role.ASSISTANT) {
            return null;
        }
        int userId = user.getId();
        return repository.getFormsForAssistant(userId);
    }

    @Override
    public void updateForm(Form form) throws Exception {
        repository.updateForm(form);
    }

    @Override
    public void finishFormWork(Form form) throws Exception {
        form.setStatus(FormStatus.FINISHED);
        repository.updateForm(form);
        Request request = form.getRequest();
        List<Form> forms = repository.getFormsByRequest(request);
        boolean allFormsFinished = true;
        for (Form f : forms) {
            if (f.getStatus() != FormStatus.FINISHED) {
                allFormsFinished = false;
                break;
            }
        }
        if (allFormsFinished) {
            request.setStatus(RequestStatus.FINISHED);
            repository.updateRequest(request);
        }
    }

    @Override
    public List<Form> getFormsByRequest(Request request) throws Exception {
        return repository.getFormsByRequest(request);
    }

    @Override
    public List<String> getAnalysisList() throws Exception {
        return repository.getAnalysisList();
    }

    @Override
    public List<TimeSpan> getTimeForAppointment(Request request) throws Exception {
        ScheduleHelper scheduleHelper = new ScheduleHelper(repository, request);
        return scheduleHelper.getAvailableStartTimesForCollection();
    }

    @Override
    public void sendToRandomAssistant(Request request, Time time) throws Exception {
        ScheduleHelper scheduleHelper = new ScheduleHelper(repository, request);
        Assistant assistant = scheduleHelper.chooseRandomAssistant(time);
        if (assistant == null) {
            throw new BadChoosedTimeException();
        }
        request.setArrivalTime(addTimeToDate(request.getArrivalTime(), time));
        editRequest(request);
        addForms(request, assistant);
    }

    private java.util.Date addTimeToDate(java.util.Date date, Time time) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                time.getHour(), time.getMinute(), 0);
        return c.getTime();
    }

    public void checkRequest(Request request) throws Exception {
        StringTypeVerifier.notEmptyString(request.getSurname(), "Фамилия должна быть указана");
        StringTypeVerifier.notEmptyString(request.getName(), "Имя должно быть указано");
        StringTypeVerifier.notEmptyString(request.getPatronymic(), "Отчество должно быть указано");
        if (request.getAnalysisList().size() <= 0) {
            throw new Exception("Необходимо выбрать хотя бы 1 анализ");
        }
    }

    @Override
    public void cancelRequest(Request request) throws Exception {
        request.setStatus(RequestStatus.CANCELED);
        repository.updateRequest(request);
    }

    @Override
    public String getProtocol(String analysis) throws Exception {
        return repository.getProtocol(analysis);
    }
}
