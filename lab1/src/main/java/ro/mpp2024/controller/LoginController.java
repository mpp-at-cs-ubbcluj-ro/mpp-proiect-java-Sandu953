package ro.mpp2024.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.util.SystemPropertiesPropertySource;
import ro.mpp2024.StartApplication;
import ro.mpp2024.service.ServiceAgentie;
import ro.mpp2024.service.ServiceExcursie;
import ro.mpp2024.service.ServiceRezervare;


import java.io.IOException;

public class LoginController {
    public Button btnLogin;
    public TextField getUser;
    public PasswordField getPassword;
    ServiceAgentie serviceAgentie;
    ServiceExcursie serviceExcursie;
    ServiceRezervare serviceRezervare;

    public void setService(ServiceAgentie serviceAgentie, ServiceExcursie serviceExcursie, ServiceRezervare serviceRezervare) {
        this.serviceAgentie = serviceAgentie;
        this.serviceExcursie = serviceExcursie;
        this.serviceRezervare = serviceRezervare;
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) throws IOException {
        try {
            String user = getUser.getText();
            String pass = getPassword.getText();
            if (serviceAgentie.handleLogin(user, pass)) {
                FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/views/rezervareView.fxml"));
                Parent root = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                RezervareController rezervareController = fxmlLoader.getController();
                rezervareController.setService(serviceExcursie, serviceRezervare);
                stage.show();
            } else {
                MessageAlert.showErrorMessage(null, "Username sau parola incorecte!");
            }

        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare la logare!" + e.getMessage());
            System.out.println(e.getMessage());
        }

    }

}
