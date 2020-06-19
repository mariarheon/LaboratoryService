package com.spbstu.dbo;

import com.spbstu.util.EAN13Generator;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class Form {
    private int id;
    private Request request;
    private String analysis;
    private Assistant assistant;
    private String barcode;
    private FormStatus status;
    private List<FormField> fields;
    private Date collectionStartDateTime;
    private Date collectionEndDateTime;
    private Date researchStartDateTime;
    private Date researchEndDateTime;

    public Date getCollectionStartDateTime() {
        return collectionStartDateTime;
    }

    public void setCollectionStartDateTime(Date collectionStartDateTime) {
        this.collectionStartDateTime = collectionStartDateTime;
    }

    public Date getCollectionEndDateTime() {
        return collectionEndDateTime;
    }

    public void setCollectionEndDateTime(Date collectionEndDateTime) {
        this.collectionEndDateTime = collectionEndDateTime;
    }

    public Date getResearchStartDateTime() {
        return researchStartDateTime;
    }

    public void setResearchStartDateTime(Date researchStartDateTime) {
        this.researchStartDateTime = researchStartDateTime;
    }

    public Date getResearchEndDateTime() {
        return researchEndDateTime;
    }

    public void setResearchEndDateTime(Date researchEndDateTime) {
        this.researchEndDateTime = researchEndDateTime;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    public FormStatus getStatus() {
        return status;
    }

    public void setStatus(FormStatus status) {
        this.status = status;
    }

    public Form() {
        barcode = EAN13Generator.generate();
        status = FormStatus.IN_WORK;
    }

    public String getRequestSurname() {
        return request.getSurname();
    }

    public String getRequestName() {
        return request.getName();
    }

    public String getRequestPatronymic() {
        return request.getPatronymic();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Assistant getAssistant() {
        return assistant;
    }

    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
