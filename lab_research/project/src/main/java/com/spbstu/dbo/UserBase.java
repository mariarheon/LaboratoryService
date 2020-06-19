/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.dbo;

import com.spbstu.exceptions.AuthException;
import com.spbstu.storage.StorageRepository;
import com.spbstu.util.StringTypeVerifier;

public abstract class UserBase implements User {
    private int id;
    private String login;
    private String phone;
    private String surname;
    private String name;
    private String patronymic;
    private Sex sex;
    private int passportSeries;
    private int passportNumber;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public int getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(int passportSeries) {
        this.passportSeries = passportSeries;
    }

    public int getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(int passportNumber) {
        this.passportNumber = passportNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public Role getRole() {
        return null;
    }

    public String getPhone() {
        return phone;
    }

    public static UserBase createByRole(Role role) {
        switch (role) {
            case ADMIN:
                return new Admin();
            case CLIENT:
                return new Client();
            case ASSISTANT:
                return new Assistant();
        }
        return null;
    }

    @Override
    public void validate() throws Exception {
        StringTypeVerifier.notEmptyString(login, "Введите логин");
        StringTypeVerifier.notEmptyString(surname, "Фамилия должна быть указана");
        StringTypeVerifier.notEmptyString(name, "Имя должно быть указано");
        StringTypeVerifier.notEmptyString(patronymic, "Отчество должно быть указано");
        StringTypeVerifier.notEmptyString(phone, "Введите телефон");
    }
}
