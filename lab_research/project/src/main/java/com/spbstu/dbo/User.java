package com.spbstu.dbo;

/**
 *
 */
public interface User {
    void setLogin(String login);
    void setPhone(String phone);
    String getSurname();
    void setSurname(String surname);
    String getName();
    void setName(String name);
    String getPatronymic();
    void setPatronymic(String patronymic);
    Sex getSex();
    void setSex(Sex sex);
    int getPassportSeries();
    void setPassportSeries(int passportSeries);
    int getPassportNumber();
    void setPassportNumber(int passportNumber);
    int getId();
    void setId(int id);
    String getLogin();
    Role getRole();
    String getPhone();
    void validate() throws Exception;
}
