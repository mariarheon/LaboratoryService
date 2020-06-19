package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.*;
import com.spbstu.facade.Facade;
import com.spbstu.util.StringTypeVerifier;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class RetrieveProtocolController {
    private Facade facade = Main.facade;

    private Form form;

    @FXML
    private Label lblProtocolText;

    @FXML
    private Label lblErrorMessage;

    @FXML
    private ScrollPane scrollPane;

    public void setData(Form form) {
        this.form = form;
        String analysis = form.getAnalysis();
        try {
            String protocol = facade.getProtocol(analysis);
            lblProtocolText.setText(protocol);
        } catch (Exception ex) {
            lblErrorMessage.setText(ex.getMessage());
        }
    }

    @FXML
    private void onBtnBackClick() {
        Main.showBlankEdit(form);
    }
}
