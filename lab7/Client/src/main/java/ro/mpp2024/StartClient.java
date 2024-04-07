package ro.mpp2024;

import ro.mpp2024.gui.*;
import ro.mpp2024.IServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2024.rpcprotocol.ServicesRpcProxy;

import java.io.IOException;
import java.util.Properties;


public class StartClient extends Application {
    private Stage primaryStage;

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";


    public void start(Stage primaryStage) throws Exception, IOException {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartClient.class.getResourceAsStream("/chatclient.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("chat.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("chat.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IServices server = new ServicesRpcProxy(serverIP, serverPort);



        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("LoginW.fxml"));
        Parent root=loader.load();


        LoginController ctrl =
                loader.<LoginController>getController();
        ctrl.setServer(server);


        FXMLLoader cloader = new FXMLLoader(
                getClass().getClassLoader().getResource("ChatW.fxml"));
        Parent croot=cloader.load();


        RezervareController chatCtrl =
                cloader.<RezervareController>getController();
        chatCtrl.setServer(server);

        ctrl.setRezervareController(chatCtrl);
        ctrl.setParent(croot);

        primaryStage.setTitle("MPP chat");
        primaryStage.setScene(new Scene(root, 300, 130));
        primaryStage.show();

    }


}


