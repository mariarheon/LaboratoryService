package com.spbstu.tests;

import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import org.junit.Assert;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 */
public class FormWorker {
    private Facade facade;
    private List<Form> generatedForms;
    private Form editedForm;

    public Facade getFacade() {
        return facade;
    }

    public List<Form> getGeneratedForms() {
        return generatedForms;
    }

    public Form getEditedForm() {
        return editedForm;
    }

    private void createForms(int analysisCnt) throws Exception {
        Facade adminFacade = new FacadeImpl();

        adminFacade.auth("admin", "pass");
        Request request = Util.addRequest(analysisCnt);
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
        assistant1Forms = assistant1Forms
                .stream()
                .filter(item -> item.getRequest().getId() == request.getId())
                .collect(Collectors.toList());
        assistant2Forms = assistant2Forms
                .stream()
                .filter(item -> item.getRequest().getId() == request.getId())
                .collect(Collectors.toList());
        Assert.assertTrue("forms was not added/passed to assistants =(",
                assistant1Forms.size() > 0 || assistant2Forms.size() > 0);
        if (assistant1Forms.size() > 0) {
            assistantFacade.auth("assistant1", "pass");
            generatedForms = assistant1Forms;
        } else {
            assistantFacade.auth("assistant2", "pass");
            generatedForms = assistant2Forms;
        }
        facade = assistantFacade;
    }

    public void createFilledForm(int analysisCnt) throws Exception {
        createForms(analysisCnt);
        List<Form> forms = generatedForms;
        Form someForm = facade.getForms().get(0);
        List<FormField> fields = someForm.getFields();
        for (FormField field : fields) {
            field.setValue(String.valueOf(ThreadLocalRandom.current().nextInt(10)));
        }
        facade.updateForm(someForm);
        Optional<Form> editedFormOpt = facade
                .getForms()
                .stream()
                .filter(item -> item.getId() == someForm.getId()).findAny();
        Assert.assertTrue("edited form is not present in database", editedFormOpt.isPresent());
        editedForm = editedFormOpt.get();
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
    }
}
