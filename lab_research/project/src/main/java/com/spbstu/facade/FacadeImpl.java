/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.facade;

import com.spbstu.dbo.*;
import com.spbstu.storage.StorageRepository;

import java.util.List;

public class FacadeImpl implements Facade {
    private StorageRepository repository = StorageRepository.getInstance();
    private User currentUser;

    public FacadeImpl() {
    }

    @Override
    public void register(User user, String password) throws Exception {
        repository.addUser(user, password);
    }

    @Override
    public List<User> getAssistants() throws Exception {
        return repository.getUsersByRole(Role.ASSISTANT);
    }

    @Override
    public void auth(String login, String password) throws Exception {
        currentUser = repository.authenticateUser(login, password);
    }

    @Override
    public List<Request> getRequestsForClient() throws Exception {
        return repository.getRequestsForClient(currentUser);
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
        repository.addRequest(request);
    }

    @Override
    public void editRequest(Request request) throws Exception {
        repository.editRequest(request);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void addForms(Request request, User assistant) throws Exception {
        List<String> analysisList = request.getAnalysisList();
        for (String analysis : analysisList) {
            Form form = new Form();
            form.setRequest(request);
            form.setAnalysis(analysis);
            form.setAssistant(assistant);
            repository.addForm(form);
        }
        request.setStatus(RequestStatus.IN_WORK);
        repository.editRequest(request);
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
}
