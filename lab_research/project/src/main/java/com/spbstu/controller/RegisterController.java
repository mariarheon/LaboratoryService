/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Role;
import com.spbstu.dbo.Sex;
import com.spbstu.dbo.User;
import com.spbstu.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    private Facade facade = Main.facade;

    @FXML private TextField tfLogin;
    @FXML private TextField tfPhone;
    @FXML private ComboBox<String> cbRole;
    @FXML private PasswordField tfPass;
    @FXML private PasswordField tfPassConfirm;
    @FXML private Label lblErrorMessage;
    @FXML
    private TextField tfSurname;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfPatronymic;
    @FXML
    private ComboBox<String> cbSex;
    @FXML
    private TextField tfPassportSeries;
    @FXML
    private TextField tfPassportNumber;

    @FXML
    private void initialize() {
        for (Role role : Role.values()) {
            cbRole.getItems().add(role.toString());
        }
        cbSex.getItems().setAll("Женский", "Мужской");
    }

    @FXML
    private void onBtnRegisterClick() {
        String login = tfLogin.getText();
        String phone = tfPhone.getText();
        String pass = tfPass.getText();
        String passConfirmation = tfPassConfirm.getText();
        String role = cbRole.getValue();
        if (login.isEmpty()) {
            lblErrorMessage.setText("Введите логин");
            return;
        }
        if (tfSurname.getText().equals("")) {
            lblErrorMessage.setText("Фамилия должна быть указана");
            return;
        }
        if (tfName.getText().equals("")) {
            lblErrorMessage.setText("Имя должно быть указано");
            return;
        }
        if (tfPatronymic.getText().equals("")) {
            lblErrorMessage.setText("Отчество должно быть указано");
            return;
        }
        String sex = cbSex.getValue();
        if (sex == null) {
            lblErrorMessage.setText("Пол должен быть указан");
            return;
        }
        int passportSeries = 0, passportNumber = 0;
        try {
            passportSeries = Integer.parseInt(tfPassportSeries.getText());
        } catch (NumberFormatException ex) {
            lblErrorMessage.setText("Серия паспорта должна быть числом");
            return;
        }
        try {
            passportNumber = Integer.parseInt(tfPassportNumber.getText());
        } catch (NumberFormatException ex) {
            lblErrorMessage.setText("Номер паспорта должен быть числом");
            return;
        }
        if (phone.isEmpty()) {
            lblErrorMessage.setText("Введите телефон");
            return;
        }
        if (role == null) {
            lblErrorMessage.setText("Роль должна быть указана");
            return;
        }
        if (pass.isEmpty()) {
            lblErrorMessage.setText("Введите пароль");
            return;
        }
        if (passConfirmation.isEmpty()) {
            lblErrorMessage.setText("Введите пароль повторно");
            return;
        }
        if (!pass.equals(passConfirmation)) {
            lblErrorMessage.setText("Введённые пароли не совпадают");
            return;
        }

        try {
            User user = new User();
            user.setLogin(login);
            user.setRole(Role.getByStr(role));
            user.setPhone(phone);
            user.setSurname(tfSurname.getText());
            user.setName(tfName.getText());
            user.setPatronymic(tfPatronymic.getText());
            user.setSex(Sex.getByStr(cbSex.getValue()));
            user.setPassportSeries(passportSeries);
            user.setPassportNumber(passportNumber);
            facade.register(user, pass);
        } catch (Exception e) {
            lblErrorMessage.setText(e.getMessage());
            return;
        }
        Main.showSignIn();
    }

    @FXML
    private void onBtnBackClick() {
        Main.showSignIn();
    }
}
