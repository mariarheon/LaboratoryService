package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class ChooseTimeController {
    private Facade facade = Main.facade;

    private Request request;

    @FXML
    private GridPane paneTimes;

    @FXML
    private TextField tfTime;

    @FXML
    private Label lblErrorMessage;

    @FXML
    private void initialize() {
    }

    @FXML
    public void setRequest(Request request) {
        this.request = request;
        try {
            facade.editRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            lblErrorMessage.setText(ex.getMessage());
        }
        List<TimeSpan> freeTime = null;
        try {
            freeTime = facade.getTimeForAppointment(request);
        } catch (Exception ex) {
            lblErrorMessage.setText(ex.getMessage());
            return;
        }
        for (int i = 0; i < freeTime.size(); i++) {
            Label lblStartTime = new Label(freeTime.get(i).getStartTime().toString());
            Label lblEndTime = new Label(freeTime.get(i).getEndTime().toString());
            paneTimes.add(lblStartTime, 0, (i + 1));
            paneTimes.add(lblEndTime, 1, (i + 1));
        }
    }

    @FXML
    private void onBtnBackClick() {
        back();
    }

    @FXML
    private void onBtnApplyClick() {
        Time time = null;
        try {
            time = Time.parse(tfTime.getText());
        } catch (Exception ex) {
            lblErrorMessage.setText("Время должно быть указано в формате HH:mm");
            return;
        }
        try {
            facade.sendToRandomAssistant(request, time);
        } catch (Exception ex) {
            ex.printStackTrace();
            lblErrorMessage.setText(ex.getMessage());
            return;
        }
        Main.showAdminMain();
    }

    private void back() {
        if (facade.getCurrentUser().getRole() == Role.ADMIN) {
            Main.showEditRequest(request);
        }
    }
}
