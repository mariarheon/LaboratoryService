package com.spbstu.tests;

import com.spbstu.dbo.Form;
import com.spbstu.dbo.FormField;
import com.spbstu.dbo.Request;
import com.spbstu.dbo.RequestStatus;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ClientTest {
    @Before
    public void setup() throws Exception {
        Util.reviveDB();
    }

    @After
    public void teardown() throws Exception {
        Util.reviveDB();
    }

    @Test
    public void addRequestTest() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("client1", "pass");
        Request request = Util.addRequest(3);
        List<Request> requests = facade.getRequestsForClient();
        Assert.assertTrue("id was not changed for new request", request.getId() != 0);
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
        Assert.assertEquals("analysis list was wrongly added for new request", facade.getAnalysisList().size(), foundRequest.getAnalysisList().size());
        facade.logout();
    }

    @Test
    public void getResultTest() throws Exception {
        FormWorker worker = new FormWorker();
        worker.createFilledForm(1);
        Facade adminFacade = worker.getFacade();
        Form editedForm = worker.getEditedForm();
        adminFacade.finishFormWork(editedForm);
        Facade clientFacade = new FacadeImpl();
        clientFacade.auth("client1", "pass");
        int reqRequestID = editedForm.getRequest().getId();
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
    public void cancelRequestTest() throws Exception {
        Request request = Util.addRequest(1);
        Facade facade = new FacadeImpl();
        facade.auth("client1", "pass");
        facade.cancelRequest(request);
        facade.logout();
    }

    @Test
    public void editRequestTest() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("client1", "pass");
        Request request = Util.addRequest(3);
        request.setPassportNumber(8889);
        facade.editRequest(request);
        List<Request> requests = facade.getRequestsForClient();
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
        Assert.assertEquals("analysis list was wrongly added for new request", facade.getAnalysisList().size(), foundRequest.getAnalysisList().size());
        facade.logout();
    }
}
