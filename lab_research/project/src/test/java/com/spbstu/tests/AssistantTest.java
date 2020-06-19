package com.spbstu.tests;

import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 */
public class AssistantTest {
    @Before
    public void setup() throws Exception {
        Util.reviveDB();
    }

    @After
    public void teardown() throws Exception {
        Util.reviveDB();
    }

    @Test
    public void editFormTest() throws Exception {
        FormWorker worker = new FormWorker();
        worker.createFilledForm(3);
    }

    @Test
    public void finishFormTest() throws Exception {
        FormWorker worker = new FormWorker();
        worker.createFilledForm(3);
        Facade facade = worker.getFacade();
        Form editedForm = worker.getEditedForm();
        facade.finishFormWork(editedForm);
        Optional<Form> foundForm = facade.getForms()
                .stream()
                .filter(item -> item.getId() == editedForm.getId())
                .findAny();
        Assert.assertTrue("form should not be presented after finishing",
                !foundForm.isPresent());
    }
}
