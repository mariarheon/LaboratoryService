package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Assistant;
import com.spbstu.dbo.Request;
import com.spbstu.dbo.UserBase;
import com.spbstu.facade.Facade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ChooseAssistantController {
    private Facade facade = Main.facade;

    private Request request;

    @FXML
    private TableView<UserBase> tableAssistants;

    @FXML
    private TableColumn<UserBase, String> colSurname;
    @FXML
    private TableColumn<UserBase, String> colName;
    @FXML
    private TableColumn<UserBase, String> colPatronymic;
    @FXML
    private TableColumn<UserBase, String> colBtnChoose;

    @FXML
    private Label lblErrorMessage;

    @FXML
    private void initialize() {
        List<Assistant> assistantList = new ArrayList<Assistant>();
        try {
            assistantList = facade.getAssistants();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        colBtnChoose.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        // setBtnChooseCellFactory();

        ObservableList<UserBase> items = FXCollections.observableArrayList();
        items.addAll(assistantList);
        tableAssistants.setItems(items);
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    /*

    private void setBtnChooseCellFactory() {
        Callback<TableColumn<User, String>, TableCell<User, String>> btnDisplayCellFactory
                = new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                final TableCell<User, String> cell = new TableCell<User, String>() {

                    final Button btn = new Button("Выбрать");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("btn-default");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                User assistant = getTableView().getItems().get(getIndex());
                                try {
                                    facade.addForms(request, assistant);
                                } catch (Exception ex) {
                                    lblErrorMessage.setText(ex.getMessage());
                                    return;
                                }
                                Main.showAdminMain();
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        colBtnChoose.setCellFactory(btnDisplayCellFactory);
    }

    */

    @FXML
    private void onBtnBackClick() {
        Main.showAdminMain();
    }
}
