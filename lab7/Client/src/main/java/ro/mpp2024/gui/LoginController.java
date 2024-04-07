package ro.mpp2024.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ro.mpp2024.Agentie;
import ro.mpp2024.Exception;
import ro.mpp2024.IServices;

import java.io.IOException;

public class LoginController {
    public Button btnLogin;
    public TextField getUser;
    public PasswordField getPassword;

    private IServices server;

    private Parent mainChatParent;

    private Agentie agentie;

    private RezervareController rezervareController;



    public void setService(IServices server) {
        this.server = server;
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) throws IOException {
//        try {
//            String user = getUser.getText();
//            String pass = getPassword.getText();
//
//            if (server.handleLogin(user, pass,)) {
//                FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/views/rezervareView.fxml"));
//                Parent root = (Parent) fxmlLoader.load();
//                Stage stage = new Stage();
//                stage.setScene(new Scene(root));
//                RezervareController rezervareController = fxmlLoader.getController();
//                rezervareController.setService(serviceExcursie, serviceRezervare);
//                stage.show();
//            } else {
//                MessageAlert.showErrorMessage(null, "Username sau parola incorecte!");
//            }
//
//        } catch (Exception e) {
//            MessageAlert.showErrorMessage(null, "Eroare la logare!" + e.getMessage());
//            System.out.println(e.getMessage());
//        }
        //Parent root;
        String nume = getUser.getText();
        String passwd = getPassword.getText();
        agentie = new Agentie(nume);


        try{
            server.handleLogin(nume, passwd, rezervareController);
            // Util.writeLog("User succesfully logged in "+crtUser.getId());
            Stage stage=new Stage();
            stage.setTitle("Window for " +agentie.getUsername());
            stage.setScene(new Scene(mainChatParent));

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    rezervareController.logout();
                    System.exit(0);
                }
            });

            stage.show();
            rezervareController.setUser(agentie);
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        }   catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MPP chat");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Wrong username or password");
            alert.showAndWait();
        }

    }

    public void setServer(IServices server) {
        this.server = server;
    }

    public void setRezervareController(RezervareController rezervareController) {
        this.rezervareController = rezervareController;
    }

    public void setParent(Parent p) {
        mainChatParent=p;
    }
}
