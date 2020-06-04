package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Form;
import com.spbstu.dbo.Request;
import com.spbstu.facade.Facade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FormsForRequestController {
    private Facade facade = Main.facade;

    @FXML
    private TableView<Form> tableForms;

    @FXML
    private TableColumn<Form, String> colBarcode;
    @FXML
    private TableColumn<Form, String> colSurname;
    @FXML
    private TableColumn<Form, String> colName;
    @FXML
    private TableColumn<Form, String> colPatronymic;
    @FXML
    private TableColumn<Form, String> colAnalysis;
    @FXML
    private TableColumn<Form, String> colBtnDisplay;

    @FXML
    private Label lblErrorMessage;

    @FXML
    private void initialize() {
    }

    public void setRequest(Request request) {
        List<Form> formList = new ArrayList<Form>();
        try {
            formList = facade.getFormsByRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("requestSurname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("requestName"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("requestPatronymic"));
        colAnalysis.setCellValueFactory(new PropertyValueFactory<>("analysis"));
        colBtnDisplay.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        setBtnDisplayCellFactory();

        ObservableList<Form> items = FXCollections.observableArrayList();
        items.addAll(formList);
        tableForms.setItems(items);
    }

    private void setBtnDisplayCellFactory() {
        Callback<TableColumn<Form, String>, TableCell<Form, String>> btnDisplayCellFactory
                = new Callback<TableColumn<Form, String>, TableCell<Form, String>>() {
            @Override
            public TableCell<Form, String> call(TableColumn<Form, String> param) {
                final TableCell<Form, String> cell = new TableCell<Form, String>() {

                    final Button btn = new Button("Просмотр");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("btn-default");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Form form = getTableView().getItems().get(getIndex());
                                Main.showBlankDisplay(form);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        colBtnDisplay.setCellFactory(btnDisplayCellFactory);
    }

    @FXML
    private void onBtnBackClick() {
        Main.showClientMain();
    }
}
