/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Role;
import com.spbstu.dbo.Sex;
import com.spbstu.dbo.User;
import com.spbstu.dbo.UserBase;
import com.spbstu.facade.Facade;
import com.spbstu.util.StringTypeVerifier;
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
        try {
            String pass = tfPass.getText();
            String passConfirmation = tfPassConfirm.getText();
            int passportSeries = StringTypeVerifier.integer(tfPassportSeries.getText(), "Серия паспорта должна быть числом");
            int passportNumber = StringTypeVerifier.integer(tfPassportNumber.getText(), "Номер паспорта должен быть числом");
            StringTypeVerifier.notEmptyString(cbSex.getValue(), "Пол должен быть указан");
            StringTypeVerifier.notEmptyString(cbRole.getValue(), "Роль должна быть выбрана");
            if (!pass.equals(passConfirmation)) {
                lblErrorMessage.setText("Введённые пароли не совпадают");
                return;
            }
            Role role = Role.getByStr(cbRole.getValue());
            User user = UserBase.createByRole(role);
            user.setLogin(tfLogin.getText());
            user.setPhone(tfPhone.getText());
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
