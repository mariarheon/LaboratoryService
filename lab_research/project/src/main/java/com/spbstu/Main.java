package com.spbstu;

import com.spbstu.dbo.Form;
import com.spbstu.dbo.Request;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import com.spbstu.controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Main extends Application {

    public static Facade facade = new FacadeImpl();
    private static Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("JavaFX version: " + com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        mainStage = stage;
        mainStage.setTitle("Лаборатория исследований");
        showSignIn();
        mainStage.show();
        // facade.auth("client1", "pass");
        // showClientMain();
    }

    public static void showSignIn() {
        showView("SignIn");
    }

    public static void showRegister() {
        showView("Register");
    }

    public static void showClientMain() {
        showView("ClientMain");
    }

    public static void showAdminMain() {
        showView("AdminMain");
    }

    public static void showAddRequest() {
        showView("AddRequest");
    }

    public static void showAssistantMain() {
        showView("AssistantMain");
    }

    public static void showEditRequest(Request request) {
        try {
            String fxmlFile = "/fxml/AddRequest.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(Main.class.getResourceAsStream(fxmlFile));
            AddRequestController controller = loader.getController();
            controller.edit(request);
            Scene scene = new Scene(root, 800, 600);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showChooseTime(Request request) {
        try {
            String fxmlFile = "/fxml/ChooseTime.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(Main.class.getResourceAsStream(fxmlFile));
            ChooseTimeController controller = loader.getController();
            controller.setRequest(request);
            Scene scene = new Scene(root, 800, 600);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showBlankEdit(Form form) {
        try {
            String fxmlFile = "/fxml/BlankEdit.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(Main.class.getResourceAsStream(fxmlFile));
            BlankEditController controller = loader.getController();
            controller.setForm(form);
            Scene scene = new Scene(root, 800, 600);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showBlankDisplay(Form form) {
        try {
            String fxmlFile = "/fxml/BlankDisplay.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(Main.class.getResourceAsStream(fxmlFile));
            BlankDisplayController controller = loader.getController();
            controller.setForm(form);
            Scene scene = new Scene(root, 800, 600);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showFormsForRequest(Request request) {
        try {
            String fxmlFile = "/fxml/FormsForRequest.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(Main.class.getResourceAsStream(fxmlFile));
            FormsForRequestController controller = loader.getController();
            controller.setRequest(request);
            Scene scene = new Scene(root, 800, 600);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showChooseAssistant(Request request) {
        try {
            String fxmlFile = "/fxml/ChooseAssistant.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(Main.class.getResourceAsStream(fxmlFile));
            ChooseAssistantController controller = loader.getController();
            controller.setRequest(request);
            Scene scene = new Scene(root, 800, 600);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showView(String viewName) {
        try {
            String fxmlFile = "/fxml/" + viewName + ".fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(Main.class.getResourceAsStream(fxmlFile));
            Scene scene = new Scene(root, 800, 600);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public static void showMainView(String user) {
        try {
            String fxmlFile = "/fxml/MainView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            MainViewController uvc = loader.getController();
            uvc.setup(user);
            Scene scene = new Scene(root, 600, 400);
            mainStage.setScene(scene);
            mainStage.setOnCloseRequest(windowEvent -> Platform.exit());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

}
