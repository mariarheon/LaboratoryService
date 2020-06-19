package com.spbstu.businessTests;

import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import com.spbstu.tests.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
public class MakeAnalysisTest {
    private Request request;
    private Form someForm;

    @Before
    public void setup() throws Exception {
        Util.reviveDB();
    }

    @After
    public void teardown() throws Exception {
        Util.reviveDB();
    }

    private void addRequestByClient() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("client1", "pass");
        request = new Request();
        request.setSurname("Осокин");
        request.setName("Станислав");
        request.setPatronymic("Владимирович");
        request.setStatus(RequestStatus.CREATED);
        request.setPassportSeries(123444);
        request.setPassportNumber(8888);
        List<String> srcAnalysisList = facade.getAnalysisList();
        List<String> analysisList = new ArrayList<>();
        analysisList.add("Клинический анализ крови");
        request.setAnalysisList(analysisList);
        Calendar c = Calendar.getInstance();
        c.clear();
        // thursday
        c.set(2030, Calendar.JANUARY, 10);
        request.setArrivalTime(c.getTime());
        request.setSex(Sex.Male);
        request.setClient((Client)facade.getCurrentUser());
        facade.addRequest(request);
        facade.logout();
        Assert.assertTrue("id was not changed for new request", request.getId() != 0);
        checkClientCanSeeRequest();
    }

    private void checkClientCanSeeRequest() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("client1", "pass");
        List<Request> requests = facade.getRequestsForClient();
        Optional<Request> foundRequestOpt = requests.stream().filter(req -> req.getId() == request.getId()).findAny();
        Assert.assertTrue("request was not added =(", foundRequestOpt.isPresent());
        Request foundRequest = foundRequestOpt.get();
        Assert.assertEquals("surname was wrongly set for request", "Осокин", foundRequest.getSurname());
        Assert.assertEquals("name was wrongly set for request", "Станислав", foundRequest.getName());
        Assert.assertEquals("patronymic was wrongly set for request", "Владимирович", foundRequest.getPatronymic());
        Assert.assertEquals("status was wrongly set for request", RequestStatus.CREATED, foundRequest.getStatus());
        Assert.assertEquals("passport series was wrongly set for request", 123444, foundRequest.getPassportSeries());
        Assert.assertEquals("passport number was wrongly set for request", 8888, foundRequest.getPassportNumber());
        Calendar c = Calendar.getInstance();
        c.clear();
        // thursday
        c.set(2030, Calendar.JANUARY, 10);
        Assert.assertTrue("arrival time was wrongly set for request", Math.abs(c.getTime().getTime() - foundRequest.getArrivalTime().getTime()) < 1000);
        Assert.assertEquals("analysis list was wrongly added for new request", 1, foundRequest.getAnalysisList().size());
        facade.logout();
    }

    private void findRequestByAdmin() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("admin", "pass");
        List<Request> requests = facade.getRequests();
        Optional<Request> foundRequestOpt = requests.stream().filter(req -> req.getId() == request.getId()).findAny();
        Assert.assertTrue("request was not added =(", foundRequestOpt.isPresent());
        Request foundRequest = foundRequestOpt.get();
        Assert.assertEquals("surname was wrongly set for request", "Осокин", foundRequest.getSurname());
        Assert.assertEquals("name was wrongly set for request", "Станислав", foundRequest.getName());
        Assert.assertEquals("patronymic was wrongly set for request", "Владимирович", foundRequest.getPatronymic());
        Assert.assertEquals("status was wrongly set for request", RequestStatus.CREATED, foundRequest.getStatus());
        Assert.assertEquals("passport series was wrongly set for request", 123444, foundRequest.getPassportSeries());
        Assert.assertEquals("passport number was wrongly set for request", 8888, foundRequest.getPassportNumber());
        Calendar c = Calendar.getInstance();
        c.clear();
        // thursday
        c.set(2030, Calendar.JANUARY, 10);
        Assert.assertTrue("arrival time was wrongly set for request", Math.abs(c.getTime().getTime() - foundRequest.getArrivalTime().getTime()) < 1000);
        Assert.assertEquals("analysis list was wrongly added for new request", 1, foundRequest.getAnalysisList().size());
        facade.logout();
    }

    private void requestFinishedByAdmin() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("admin", "pass");
        List<Request> requests = facade.getRequests();
        Optional<Request> foundRequestOpt = requests.stream().filter(req -> req.getId() == request.getId()).findAny();
        Assert.assertTrue("request was not added =(", foundRequestOpt.isPresent());
        Request foundRequest = foundRequestOpt.get();
        Assert.assertEquals("status was wrongly set for request", RequestStatus.FINISHED, foundRequest.getStatus());
        facade.logout();
    }

    private void reconcileDataByAdmin() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("admin", "pass");
        request.setPassportNumber(8889);
        facade.editRequest(request);
        List<Request> requests = facade.getRequests();
        Assert.assertTrue("id was not changed for request", request.getId() != 0);
        Optional<Request> foundRequestOpt = requests.stream().filter(req -> req.getId() == request.getId()).findAny();
        Assert.assertTrue("request is absent but should be presented after update operation =(", foundRequestOpt.isPresent());
        Request foundRequest = foundRequestOpt.get();
        Assert.assertEquals("surname was wrongly set for request", "Осокин", foundRequest.getSurname());
        Assert.assertEquals("name was wrongly set for request", "Станислав", foundRequest.getName());
        Assert.assertEquals("patronymic was wrongly set for request", "Владимирович", foundRequest.getPatronymic());
        Assert.assertEquals("status was wrongly set for request", RequestStatus.CREATED, foundRequest.getStatus());
        Assert.assertEquals("passport series was wrongly set for request", 123444, foundRequest.getPassportSeries());
        Assert.assertEquals("passport number was wrongly set for request", 8889, foundRequest.getPassportNumber());
        Calendar c = Calendar.getInstance();
        c.clear();
        // thursday
        c.set(2030, Calendar.JANUARY, 10);
        Assert.assertTrue("arrival time was wrongly set for request", Math.abs(c.getTime().getTime() - foundRequest.getArrivalTime().getTime()) < 1000);
        Assert.assertEquals("analysis list was wrongly added for new request", 1, foundRequest.getAnalysisList().size());
        facade.logout();
    }

    private void fillFormsByAdmin() throws Exception {
        Facade adminFacade = new FacadeImpl();
        adminFacade.auth("admin", "pass");
        List<TimeSpan> timeSpanList = adminFacade.getTimeForAppointment(request);
        Assert.assertTrue("failed to get time for appointment =(", timeSpanList.size() > 0);
        TimeSpan ts = timeSpanList.get(0);
        Time timeForAppointment = ts.getStartTime();
        adminFacade.sendToRandomAssistant(request, timeForAppointment);
        adminFacade.logout();
    }

    private void prepare() throws Exception {
        addRequestByClient();
        findRequestByAdmin();
        reconcileDataByAdmin();
        fillFormsByAdmin();
    }

    private void readProtocolByAssistant() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("assistant1", "pass");
        List<Form> forms = facade.getFormsByRequest(request);
        Assert.assertEquals("1 form should be here", 1, forms.size());
        Form form = forms.get(0);
        String analysis = form.getAnalysis();
        String protocolData = facade.getProtocol(analysis);
        Assert.assertTrue("protocol text was not presented =(", !protocolData.isEmpty());
        facade.logout();
    }

    private void editFormByAssistant() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("assistant1", "pass");
        List<Form> forms = facade.getFormsByRequest(request);
        Assert.assertEquals("1 form should be here", 1, forms.size());
        someForm = forms.get(0);
        List<FormField> fields = someForm.getFields();
        for (FormField field : fields) {
            field.setValue(String.valueOf(ThreadLocalRandom.current().nextInt(10)));
        }
        facade.updateForm(someForm);
        Optional<Form> editedFormOpt = facade
                .getFormsByRequest(request)
                .stream()
                .filter(item -> item.getId() == someForm.getId()).findAny();
        Assert.assertTrue("edited form is not present in database", editedFormOpt.isPresent());
        Form editedForm = editedFormOpt.get();
        List<FormField> editedFields = editedForm.getFields();
        for (FormField expectedField : fields) {
            Optional<FormField> editedFieldOpt = editedFields
                    .stream()
                    .filter(item -> item.getName().equals(expectedField.getName()))
                    .findFirst();
            Assert.assertTrue("edited form field is not present in database",
                    editedFieldOpt.isPresent());
            FormField editedField = editedFieldOpt.get();
            Assert.assertEquals("value was not updated for field in database =(",
                    expectedField.getValue(), editedField.getValue());
        }
        facade.logout();
    }

    private void finishFormByAssistant() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("assistant1", "pass");
        facade.finishFormWork(someForm);
        facade.logout();
    }

    private void readFormByClient() throws Exception {
        Facade clientFacade = new FacadeImpl();
        clientFacade.auth("client1", "pass");
        int reqRequestID = request.getId();
        Optional<Request> reqOpt = clientFacade
                .getRequests()
                .stream()
                .filter(item -> item.getId() == reqRequestID)
                .findAny();
        Assert.assertTrue("request was not found =(", reqOpt.isPresent());
        Request req = reqOpt.get();
        Assert.assertEquals("status of request should be FINISHED", RequestStatus.FINISHED, req.getStatus());
        List<Form> forms = clientFacade.getFormsByRequest(req);
        Assert.assertEquals("only one form was created but 0 or more than 1 actually have", 1, forms.size());
        Form form = forms.get(0);
        List<FormField> fields = form.getFields();
        for (FormField field : fields) {
            Assert.assertTrue("form field was not set", !field.getValue().trim().equals(""));
        }
    }

    @Test
    public void mainTest() throws Exception {
        prepare();
        readProtocolByAssistant();
        editFormByAssistant();
        finishFormByAssistant();
        requestFinishedByAdmin();
        readFormByClient();
    }
}
