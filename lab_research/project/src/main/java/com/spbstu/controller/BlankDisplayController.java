/**
 * Created by mary.mikhaleva on 01.05.20.
 */

package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Form;
import com.spbstu.dbo.FormField;
import com.spbstu.facade.Facade;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

public class BlankDisplayController {

    private Facade facade = Main.facade;

    @FXML private Label lblOutBarcode;
    @FXML private Label lblOutAnalysis;
    @FXML private Label lblOutSurname;
    @FXML private Label lblOutName;
    @FXML private Label lblOutPatronymic;
    @FXML private Label lblErrorMessage;
    @FXML private Button btnBack;
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
        final int startRowIndex = 6;
        int curRow = startRowIndex;
        List<FormField> fields = form.getFields();
        for (FormField field : fields) {
            Label labelDesc = new Label(field.getDescription());
            Label labelValue = new Label(field.getValue());
            Label labelUnits = new Label(field.getUnits());
            mainGrid.add(labelDesc, 0, curRow);
            mainGrid.add(labelValue, 1, curRow);
            mainGrid.add(labelUnits, 2, curRow);
            curRow++;
        }
        GridPane.setRowIndex(lblErrorMessage, curRow);
        curRow++;
        GridPane.setRowIndex(btnBack, curRow);
    }

    @FXML
    private void onBtnBackClick() {
        Main.showFormsForRequest(form.getRequest());
    }
}
