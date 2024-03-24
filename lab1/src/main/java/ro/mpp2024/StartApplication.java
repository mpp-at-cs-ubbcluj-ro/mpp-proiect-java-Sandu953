package ro.mpp2024;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ro.mpp2024.controller.LoginController;
import ro.mpp2024.repository.AgentieRepo;
import ro.mpp2024.repository.ExcursieRepo;
import ro.mpp2024.repository.RezervareRepo;
import ro.mpp2024.service.ServiceAgentie;
import ro.mpp2024.service.ServiceExcursie;
import ro.mpp2024.service.ServiceRezervare;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.Properties;

public class StartApplication extends Application {

    ServiceAgentie serviceAgentie;
    ServiceExcursie serviceExcursie;
    ServiceRezervare serviceRezervare;


    @Override
    public void start(Stage primaryStage) throws IOException, NoSuchAlgorithmException {
        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        ExcursieRepo excursieRepo = new ExcursieRepo(props);
        this.serviceAgentie = new ServiceAgentie(new AgentieRepo(props));
        this.serviceExcursie = new ServiceExcursie(excursieRepo);
        this.serviceRezervare = new ServiceRezervare(new RezervareRepo(props, excursieRepo));

        //serviceAgentie.saveAgentie("admin","1234");
//        serviceExcursie.addExcursie(1L,"Gradina Botanica","CTP", LocalTime.parse("12:00:00"), 100, 10);
//        serviceExcursie.addExcursie(2L,"Gradina Botanica","CTP", LocalTime.parse("13:00:00"), 100, 10);
//        serviceExcursie.addExcursie(3L,"Gradina Botanica","CTP", LocalTime.parse("14:00:00"), 100, 10);
//        serviceExcursie.addExcursie(4L,"Muzeu de Istorie","CTP", LocalTime.parse("12:00:00"), 100, 10);

        primaryStage.setTitle("START PAGE");
        startView(primaryStage);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void startView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/views/loginView.fxml"));
        AnchorPane Layout = fxmlLoader.load();
        stage.setScene(new Scene(Layout));

        LoginController startController = fxmlLoader.getController();
        startController.setService(serviceAgentie, serviceExcursie, serviceRezervare);
    }

}
