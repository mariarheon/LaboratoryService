/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;

public class BlankEditController {

    private Facade facade = Main.facade;

    @FXML private Label lblOutBarcode;
    @FXML private Label lblOutAnalysis;
    @FXML private Label lblOutSurname;
    @FXML private Label lblOutName;
    @FXML private Label lblOutPatronymic;
    @FXML private Label lblErrorMessage;
    @FXML private Button btnBack;
    @FXML private Button btnApply;
    @FXML private GridPane mainGrid;

    private Form form;

    @FXML
    private void initialize() {
    }

    public void setForm(Form form) {
        this.form = form;
        initLabels();
        initFields();
    }

    private void initLabels() {
        lblOutBarcode.setText(form.getBarcode());
        lblOutAnalysis.setText(form.getAnalysis());
        lblOutSurname.setText(form.getRequestSurname());
        lblOutName.setText(form.getRequestName());
        lblOutPatronymic.setText(form.getRequestPatronymic());
    }

    private void initFields() {
        final int startRowIndex = 7;
        int curRow = startRowIndex;
        List<FormField> fields = form.getFields();
        for (FormField field : fields) {
            Label labelDesc = new Label(field.getDescription());
            TextField tf = new TextField(field.getValue());
            tf.setUserData(field);
            Label labelUnits = new Label(field.getUnits());
            mainGrid.add(labelDesc, 0, curRow);
            mainGrid.add(tf, 1, curRow);
            mainGrid.add(labelUnits, 2, curRow);
            curRow++;
        }
        GridPane.setRowIndex(lblErrorMessage, curRow);
        curRow++;
        GridPane.setRowIndex(btnBack, curRow);
        GridPane.setRowIndex(btnApply, curRow);
    }

    @FXML
    private void onBtnApplyClick() {
        ObservableList<Node> children = mainGrid.getChildren();
        for (Node node : children) {
            if (!(node instanceof TextField)) {
                continue;
            }
            TextField tf = (TextField) node;
            Object userData = tf.getUserData();
            if (!(userData instanceof FormField)) {
                continue;
            }
            FormField ff = (FormField) userData;
            ff.setValue(tf.getText());
        }
        try {
            facade.updateForm(form);
        } catch (Exception ex) {
            lblErrorMessage.setText(ex.getMessage());
            return;
        }
        Main.showAssistantMain();
    }

    @FXML
    private void onBtnBackClick() {
        Main.showAssistantMain();
    }

    @FXML
    private void onBtnRetrieveProtocolClick() {
        Main.showRetrieveProtocol(form);
    }
}
