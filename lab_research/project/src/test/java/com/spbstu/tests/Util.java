package com.spbstu.tests;

import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import com.spbstu.storage.StorageRepository;
import org.junit.Assert;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 */
public class Util {
    public static void reviveDB() throws Exception {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream reviveScript = classloader.getResourceAsStream("db_create.sql");
        assert reviveScript != null;
        StorageRepository.getInstance().revive(reviveScript);
        reviveScript.close();
    }

    public static Request addRequest(int analysisCnt) throws Exception {
        Facade facade = new FacadeImpl();
        facade.auth("client1", "pass");
        Request request = new Request();
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
        return request;
    }
}
