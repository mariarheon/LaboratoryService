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
import java.util.stream.Collectors;

/**
 * Тест - Запись на сдачу анализов
 */
public class MakeAnAppointmentTest {
    private Request request;
    private final int analysisCnt = 3;

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
        int actualAnalysisCnt = Math.min(srcAnalysisList.size(), analysisCnt);
        for (int i = 0; i < actualAnalysisCnt; i++) {
            analysisList.add(srcAnalysisList.get(i));
        }
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
        Assert.assertEquals("analysis list was wrongly added for new request", analysisCnt, foundRequest.getAnalysisList().size());
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
        Assert.assertEquals("analysis list was wrongly added for new request", facade.getAnalysisList().size(), foundRequest.getAnalysisList().size());
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
        Assert.assertEquals("analysis list was wrongly added for new request", facade.getAnalysisList().size(), foundRequest.getAnalysisList().size());
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

    private void cancelRequestByClient() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("client1", "pass");
        facade.cancelRequest(request);
        Optional<Request> reqOpt = facade.getRequestsForClient()
                .stream()
                .filter(item -> item.getId() == request.getId())
                .findAny();
        Assert.assertTrue(reqOpt.isPresent());
        Request req = reqOpt.get();
        RequestStatus status = req.getStatus();
        Assert.assertEquals("request status should be \"canceled\"", RequestStatus.CANCELED, status);
        facade.logout();
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
    public void mainTest() throws Exception {
        addRequestByClient();
        findRequestByAdmin();
        reconcileDataByAdmin();
        fillFormsByAdmin();
    }

    @Test
    public void alternative1Test() throws Exception {
        addRequestByClient();
        cancelRequestByClient();
    }

    @Test
    public void alternative2Test() throws Exception {
        addRequestByClient();
        findRequestByAdmin();
        cancelRequestByAdmin();
    }

    public Request getRequest() {
        return request;
    }
}
