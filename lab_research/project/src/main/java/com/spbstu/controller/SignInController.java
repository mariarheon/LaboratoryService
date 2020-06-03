/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Role;
import com.spbstu.dbo.User;
import com.spbstu.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class SignInController {

    private Facade facade = Main.facade;

    @FXML private Label lblErrorMessage;
    @FXML private TextField tfLogin;
    @FXML private PasswordField tfPass;

    @FXML
    private void initialize() {

    }

    @FXML
    private void onBtnSignInClick() {
        String login = tfLogin.getText();
        String password = tfPass.getText();
        if (login.isEmpty() || password.isEmpty()) {
            lblErrorMessage.setText("Логин и пароль не могут быть пустыми");
            return;
        }
        try {
            facade.auth(login, password);
            User user = facade.getCurrentUser();
            if (user.getRole() == Role.CLIENT) {
                Main.showClientMain();
            } else if (user.getRole() == Role.ADMIN) {
                Main.showAdminMain();
            } else if (user.getRole() == Role.ASSISTANT) {
                Main.showAssistantMain();
            }
        } catch (Exception e) {
            lblErrorMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void onLblRegisterClick() throws IOException {
        Main.showRegister();
    }
}
