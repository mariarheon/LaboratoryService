package com.spbstu.tests;

import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class AdminTest {
    @Before
    public void setup() throws Exception {
        Util.reviveDB();
    }

    @After
    public void teardown() throws Exception {
        Util.reviveDB();
    }

    @Test
    public void reconcileDataTest() throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("admin", "pass");
        Request request = Util.addRequest(3);
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

    @Test
    public void sendToAssistantTest() throws Exception {
        Facade adminFacade = new FacadeImpl();

        adminFacade.auth("admin", "pass");
        Request request = Util.addRequest(3);
        List<TimeSpan> timeSpanList = adminFacade.getTimeForAppointment(request);
        Assert.assertTrue("failed to get time for appointment =(", timeSpanList.size() > 0);
        TimeSpan ts = timeSpanList.get(0);
        Time timeForAppointment = ts.getStartTime();
        adminFacade.sendToRandomAssistant(request, timeForAppointment);

        adminFacade.logout();

        Facade assistantFacade = new FacadeImpl();
        assistantFacade.auth("assistant1", "pass");
        List<Form> assistant1Forms = assistantFacade.getForms();
        assistantFacade.logout();
        assistantFacade.auth("assistant2", "pass");
        List<Form> assistant2Forms = assistantFacade.getForms();
        assistantFacade.logout();
        assistant1Forms = assistant1Forms.stream().filter(item -> item.getRequest().getId() == request.getId()).collect(Collectors.toList());
        assistant2Forms = assistant2Forms.stream().filter(item -> item.getRequest().getId() == request.getId()).collect(Collectors.toList());
        Assert.assertTrue("forms was not added/passed to assistants =(", assistant1Forms.size() > 0 || assistant2Forms.size() > 0);
        List<Form> forms = assistant1Forms.size() > 0 ? assistant1Forms : assistant2Forms;
        Assert.assertEquals("3 forms should be created for this request", 3, forms.size());
        Assert.assertEquals("form for urine analysis not presented", 1,
                forms.stream().filter(item -> item.getAnalysis().startsWith("Общий"))
                .collect(Collectors.toList()).size());
        Assert.assertEquals("form for stool analysis not presented", 1,
                forms.stream().filter(item -> item.getAnalysis().startsWith("Биохимический"))
                        .collect(Collectors.toList()).size());
        Assert.assertEquals("form for blood analysis not presented", 1,
                forms.stream().filter(item -> item.getAnalysis().startsWith("Клинический"))
                        .collect(Collectors.toList()).size());
    }

    @Test
    public void cancelRequestTest() throws Exception {
        Request request = Util.addRequest(1);
        Facade facade = new FacadeImpl();
        facade.auth("admin", "pass");
        facade.cancelRequest(request);
        facade.logout();
    }
}
