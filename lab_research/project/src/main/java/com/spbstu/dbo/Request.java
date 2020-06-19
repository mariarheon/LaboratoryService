package com.spbstu.dbo;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class Request {
    public int id;
    public String surname;
    public String name;
    public String patronymic;
    public Sex sex;
    public int passportSeries;
    public int passportNumber;
    public List<String> analysisList;
    public RequestStatus status;
    public Date arrivalTime;
    public Client client;

    public String getClientPhone() {
        return client.getPhone();
    }

    public String getClientName() {
        return client.getName();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public Sex getSex() {
        return sex;
    }

    public int getPassportSeries() {
        return passportSeries;
    }

    public int getPassportNumber() {
        return passportNumber;
    }

    public List<String> getAnalysisList() {
        return analysisList;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setPassportSeries(int passportSeries) {
        this.passportSeries = passportSeries;
    }

    public void setPassportNumber(int passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setAnalysisList(List<String> analysisList) {
        this.analysisList = analysisList;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
