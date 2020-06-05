package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
public class AddRequestController {
    private Facade facade = Main.facade;

    private boolean forEdit = false;
    private Request oldRequest;

    @FXML
    private VBox paneAnalysis;

    @FXML
    private Label lblHeader;

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
    private TextField tfArrivalTime;
    @FXML
    private Label lblErrorMessage;

    @FXML
    private Button btnApply;

    @FXML
    private void initialize() {
        List<String> analysisList = new ArrayList<String>();
        try {
            analysisList = facade.getAnalysisList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (String analysis : analysisList) {
            CheckBox cbTitle = new CheckBox(analysis);
            //cbTitle.setPrefWidth(280);
            paneAnalysis.getChildren().add(cbTitle);
        }
        cbSex.getItems().setAll("Женский", "Мужской");

        User user = facade.getCurrentUser();
        tfSurname.setText(user.getSurname());
        tfName.setText(user.getName());
        tfPatronymic.setText(user.getPatronymic());
        cbSex.setValue(user.getSex().toString());
        tfPassportSeries.setText(String.valueOf(user.getPassportSeries()));
        tfPassportNumber.setText(String.valueOf(user.getPassportNumber()));
    }

    public void edit(Request request) {
        forEdit = true;
        oldRequest = request;
        tfSurname.setText(request.getSurname());
        tfName.setText(request.getName());
        tfPatronymic.setText(request.getPatronymic());
        cbSex.setValue(request.getSex().toString());
        tfPassportSeries.setText(String.valueOf(request.getPassportSeries()));
        tfPassportNumber.setText(String.valueOf(request.getPassportNumber()));
        SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        tfArrivalTime.setText(dFormat.format(request.getArrivalTime()).toString());
        List<String> analysisList = request.getAnalysisList();
        for (Node node : paneAnalysis.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox cbNode = (CheckBox) node;
                if (analysisList.contains(cbNode.getText())) {
                    cbNode.setSelected(true);
                }
            }
        }
        lblHeader.setText("Согласование данных заявки");
        btnApply.setText("Выбрать время");
    }

    @FXML
    private void onBtnBackClick() {
        back();
    }

    @FXML
    private void onBtnApplyClick() {
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
        SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        String arrivalTimeStr = tfArrivalTime.getText();
        Date arrivalTime = null;
        try {
            arrivalTime = dFormat.parse(arrivalTimeStr);
        } catch (ParseException ex) {
            lblErrorMessage.setText("Время прибытия должно быть в формате dd.MM.yyyy");
            return;
        }
        List<String> analysisList = new ArrayList<String>();
        ObservableList<Node> analysisCtrlList = paneAnalysis.getChildren();
        for (Node analysisCtrl : analysisCtrlList) {
            if (analysisCtrl instanceof CheckBox) {
                CheckBox cbAnalysisCtrl = (CheckBox) analysisCtrl;
                if (cbAnalysisCtrl.isSelected()) {
                    analysisList.add(cbAnalysisCtrl.getText());
                }
            }
        }
        if (analysisList.size() <= 0) {
            lblErrorMessage.setText("Необходимо выбрать хотя бы 1 анализ");
            return;
        }

        Request request = new Request();
        request.setSurname(tfSurname.getText());
        request.setName(tfName.getText());
        request.setPatronymic(tfPatronymic.getText());
        request.setSex(Sex.getByStr(cbSex.getValue()));
        request.setPassportSeries(passportSeries);
        request.setPassportNumber(passportNumber);
        request.setArrivalTime(arrivalTime);
        request.setAnalysisList(analysisList);
        request.setStatus(RequestStatus.CREATED);
        if (forEdit) {
            request.setId(oldRequest.getId());
            request.setClient(oldRequest.getClient());
        } else {
            request.setClient(facade.getCurrentUser());
        }
        try {
            if (forEdit) {
                /*
                request.setStatus(oldRequest.getStatus());
                if (request.getStatus() == RequestStatus.CREATED) {
                    request.setStatus(RequestStatus.APPLIED);
                }
                */
                // facade.editRequest(request);
            } else {
                facade.addRequest(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblErrorMessage.setText(ex.getMessage());
            return;
        }
        if (forEdit) {
            Main.showChooseTime(request);
        } else {
            back();
        }
    }

    private void back() {
        if (facade.getCurrentUser().getRole() == Role.ADMIN) {
            Main.showAdminMain();
        } else if (facade.getCurrentUser().getRole() == Role.CLIENT) {
            Main.showClientMain();
        }
    }
}
