package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Form;
import com.spbstu.dbo.FormStatus;
import com.spbstu.dbo.Request;
import com.spbstu.dbo.RequestStatus;
import com.spbstu.facade.Facade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class AssistantMainController {
    private Facade facade = Main.facade;

    @FXML
    private TableView<Form> tableForms;

    @FXML
    private TableColumn<Form, Date> colCollectionStart;
    @FXML
    private TableColumn<Form, Date> colCollectionEnd;
    @FXML
    private TableColumn<Form, Date> colResearchStart;
    @FXML
    private TableColumn<Form, Date> colResearchEnd;
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
    private TableColumn<Form, String> colBtnEdit;
    @FXML
    private TableColumn<Form, String> colBtnFinish;

    @FXML
    private Label lblErrorMessage;

    @FXML
    private void initialize() {
        List<Form> formList = new ArrayList<Form>();
        try {
            formList = facade.getForms();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        colCollectionStart.setCellValueFactory(new PropertyValueFactory<>("collectionStartDateTime"));
        colCollectionEnd.setCellValueFactory(new PropertyValueFactory<>("collectionEndDateTime"));
        colResearchStart.setCellValueFactory(new PropertyValueFactory<>("researchStartDateTime"));
        colResearchEnd.setCellValueFactory(new PropertyValueFactory<>("researchEndDateTime"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("requestSurname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("requestName"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("requestPatronymic"));
        colAnalysis.setCellValueFactory(new PropertyValueFactory<>("analysis"));
        colBtnEdit.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        colBtnFinish.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        setDateTimeCellFactory(colCollectionStart);
        setDateTimeCellFactory(colCollectionEnd);
        setDateTimeCellFactory(colResearchStart);
        setDateTimeCellFactory(colResearchEnd);
        setBtnEditCellFactory();
        setBtnFinishCellFactory();

        ObservableList<Form> items = FXCollections.observableArrayList();
        items.addAll(formList);
        tableForms.setItems(items);
    }

    private void setDateTimeCellFactory(TableColumn<Form, Date> column) {
        column.setCellFactory(col -> {
            TableCell<Form, Date> cell = new TableCell<Form, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item == null) {
                            setText("Неизвестно");
                        } else {
                            setText(format.format(item));
                        }
                    }
                }
            };

            return cell;
        });
    }

    private void setBtnEditCellFactory() {
        Callback<TableColumn<Form, String>, TableCell<Form, String>> btnDisplayCellFactory
                = new Callback<TableColumn<Form, String>, TableCell<Form, String>>() {
            @Override
            public TableCell<Form, String> call(TableColumn<Form, String> param) {
                final TableCell<Form, String> cell = new TableCell<Form, String>() {

                    final Button btn = new Button("Редактировать");

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
                                Main.showBlankEdit(form);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        colBtnEdit.setCellFactory(btnDisplayCellFactory);
    }

    private void setBtnFinishCellFactory() {
        Callback<TableColumn<Form, String>, TableCell<Form, String>> btnDisplayCellFactory
                = new Callback<TableColumn<Form, String>, TableCell<Form, String>>() {
            @Override
            public TableCell<Form, String> call(TableColumn<Form, String> param) {
                final TableCell<Form, String> cell = new TableCell<Form, String>() {

                    final Button btn = new Button("Завершить работу");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("btn-default");
                        if (empty/* || getTableView().getItems().get(getIndex()).getStatus() != FormStatus.FINISHED*/) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Form form = getTableView().getItems().get(getIndex());
                                try {
                                    facade.finishFormWork(form);
                                } catch (Exception ex) {
                                    lblErrorMessage.setText(ex.getMessage());
                                    return;
                                }
                                Main.showAssistantMain();
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        colBtnFinish.setCellFactory(btnDisplayCellFactory);
    }

    @FXML
    private void onBtnBackClick() {
        facade.logout();
        Main.showSignIn();
    }
}
