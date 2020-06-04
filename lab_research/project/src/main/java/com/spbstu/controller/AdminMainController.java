package com.spbstu.controller;

import com.spbstu.Main;
import com.spbstu.dbo.Request;
import com.spbstu.dbo.RequestStatus;
import com.spbstu.facade.Facade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class AdminMainController {
    private Facade facade = Main.facade;

    @FXML
    private TableView<Request> tableRequests;

    @FXML
    private TableColumn<Request, String> colPhone;
    @FXML
    private TableColumn<Request, String> colClientName;
    @FXML
    private TableColumn<Request, String> colSurname;
    @FXML
    private TableColumn<Request, String> colName;
    @FXML
    private TableColumn<Request, String> colPatronymic;
    @FXML
    private TableColumn<Request, String> colSex;
    @FXML
    private TableColumn<Request, String> colPassportSeries;
    @FXML
    private TableColumn<Request, String> colPassportNumber;
    @FXML
    private TableColumn<Request, String> colAnalysis;
    @FXML
    private TableColumn<Request, String> colStatus;
    @FXML
    private TableColumn<Request, Date> colArrivalTime;
    @FXML
    private TableColumn<Request, String> colBtnEdit;

    @FXML
    private void initialize() {
        List<Request> requestList = new ArrayList<Request>();
        try {
            requestList = facade.getRequests();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        colPhone.setCellValueFactory(new PropertyValueFactory<>("clientPhone"));
        colClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        colSex.setCellValueFactory(new PropertyValueFactory<>("sex"));
        colPassportSeries.setCellValueFactory(new PropertyValueFactory<>("passportSeries"));
        colPassportNumber.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        colAnalysis.setCellValueFactory(new PropertyValueFactory<>("analysisList"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colArrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        colBtnEdit.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        setArrivalTimeCellFactory();
        setBtnEditCellFactory();

        ObservableList<Request> items = FXCollections.observableArrayList();
        items.addAll(requestList);
        tableRequests.setItems(items);
    }

    private void setArrivalTimeCellFactory() {
        colArrivalTime.setCellFactory(column -> {
            TableCell<Request, Date> cell = new TableCell<Request, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };

            return cell;
        });
    }

    private void setBtnEditCellFactory() {
        Callback<TableColumn<Request, String>, TableCell<Request, String>> btnDisplayCellFactory
                = new Callback<TableColumn<Request, String>, TableCell<Request, String>>() {
            @Override
            public TableCell<Request, String> call(TableColumn<Request, String> param) {
                final TableCell<Request, String> cell = new TableCell<Request, String>() {

                    final Button btn1 = new Button("Согласовать данные");
                    final Button btn2 = new Button("Передать лаборанту");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        btn1.getStyleClass().add("btn-default");
                        btn2.getStyleClass().add("btn-default");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn1.setOnAction(event -> {
                                Request request = getTableView().getItems().get(getIndex());
                                Main.showEditRequest(request);
                            });
                            btn2.setOnAction(event -> {
                                Request request = getTableView().getItems().get(getIndex());
                                Main.showChooseAssistant(request);
                            });
                            RequestStatus status = getTableView().getItems().get(getIndex()).getStatus();
                            if (status == RequestStatus.APPLIED) {
                                setGraphic(btn2);
                            } else if (status == RequestStatus.CREATED) {
                                setGraphic(btn1);
                            } else {
                                setGraphic(null);
                            }
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        colBtnEdit.setCellFactory(btnDisplayCellFactory);
    }

    @FXML
    private void onBtnBackClick() {
        facade.logout();
        Main.showSignIn();
    }
}
