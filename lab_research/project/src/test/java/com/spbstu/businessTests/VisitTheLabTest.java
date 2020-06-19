package com.spbstu.businessTests;

import com.spbstu.dbo.Form;
import com.spbstu.dbo.Request;
import com.spbstu.dbo.RequestStatus;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import com.spbstu.tests.FormWorker;
import com.spbstu.tests.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public class VisitTheLabTest {
    private Request request;

    @Before
    public void setup() throws Exception {
        Util.reviveDB();
    }

    @After
    public void teardown() throws Exception {
        Util.reviveDB();
    }

    private void changeStatusToInWorkByAdmin() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("admin", "pass");
        Optional<Request> reqOpt = facade.getRequests()
                .stream()
                .filter(item -> item.getId() == request.getId())
                .findAny();
        Assert.assertTrue(reqOpt.isPresent());
        Request req = reqOpt.get();
        req.setStatus(RequestStatus.IN_WORK);
        reqOpt = facade.getRequests()
                .stream()
                .filter(item -> item.getId() == request.getId())
                .findAny();
        Assert.assertTrue(reqOpt.isPresent());
        req = reqOpt.get();
        RequestStatus status = req.getStatus();
        Assert.assertEquals("status was not changed to \"in_work\"", RequestStatus.IN_WORK, status);
        facade.logout();
    }

    private void readFormsDataByAssistant() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("assistant1", "pass");
        List<Form> forms = facade.getFormsByRequest(request);
        Assert.assertEquals("there should be 3 forms", 3, forms.size());
        for (Form form : forms) {
            Assert.assertEquals("request id is not correct for form", request.getId(), form.getRequest().getId());
        }
        facade.logout();
    }

    @Test
    public void mainTest() throws Exception {
        MakeAnAppointmentTest makeAppointmentTest = new MakeAnAppointmentTest();
        makeAppointmentTest.mainTest();
        request = makeAppointmentTest.getRequest();
        changeStatusToInWorkByAdmin();
        readFormsDataByAssistant();
    }

    private void cancelRequestByAdmin() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("admin", "pass");
        facade.cancelRequest(request);
        Optional<Request> reqOpt = facade.getRequests()
                .stream()
                .filter(item -> item.getId() == request.getId())
                .findAny();
        Assert.assertTrue(reqOpt.isPresent());
        Request req = reqOpt.get();
        RequestStatus status = req.getStatus();
        Assert.assertEquals("request status should be \"canceled\"", RequestStatus.CANCELED, status);
        facade.logout();
    }

    @Test
    public void alternative1Test() throws Exception {
        MakeAnAppointmentTest makeAppointmentTest = new MakeAnAppointmentTest();
        makeAppointmentTest.mainTest();
        request = makeAppointmentTest.getRequest();
        cancelRequestByAdmin();
    }
}
