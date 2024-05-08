package ro.mpp2024.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ro.mpp2024.*;
import ro.mpp2024.Exception;


import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RezervareController implements  IObserver {
    public TableView<Excursie> tableView;
    public TextField getObiectiv;
    public ComboBox getOra1;
    public ComboBox getOra2;
    public Button btnCauta;
    public TextField getNume;
    public TextField getTelefon;
    public TextField getLocuri;
    public Button btnRezerva;
    public Button btnLogout;
    public TableColumn<Excursie, String> obiectiv;
    public TableColumn<Excursie, String> firma;
    public TableColumn<Excursie, LocalTime> ora;
    public TableColumn<Excursie, Integer> pret;
    public TableColumn<Excursie, Integer> nrLocuri;
    public TableColumn<Excursie, Integer> locuri2;
    public TableColumn<Excursie, Integer> pret2;
    public TableColumn<Excursie, LocalTime> ora2;
    public TableColumn<Excursie, String> firma2;
    public TableView<Excursie> tableViewRez;

    private IServices server;

    private Agentie agentie;

//    public void setService(ServiceExcursie serviceExcursie, ServiceRezervare serviceRezervare) {
//        this.serviceExcursie = serviceExcursie;
//        this.serviceRezervare = serviceRezervare;
//        initModelExcursii();
//        setCombo1();
//        setCombo2();
//    }

    ObservableList<Excursie> modelExcursii = FXCollections.observableArrayList();
    ObservableList<Excursie> modelExcursiiRezervari = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tableView.setItems(modelExcursii);

        obiectiv.setCellValueFactory(new PropertyValueFactory<>("obiectivTuristic"));
        firma.setCellValueFactory(new PropertyValueFactory<>("numeTransport"));
        ora.setCellValueFactory(new PropertyValueFactory<>("oraPlecare"));
        pret.setCellValueFactory(new PropertyValueFactory<>("pret"));
        nrLocuri.setCellValueFactory(new PropertyValueFactory<>("locuriLibere"));

        tableViewRez.setItems(modelExcursiiRezervari);

        firma2.setCellValueFactory(new PropertyValueFactory<>("numeTransport"));
        ora2.setCellValueFactory(new PropertyValueFactory<>("oraPlecare"));
        pret2.setCellValueFactory(new PropertyValueFactory<>("pret"));
        locuri2.setCellValueFactory(new PropertyValueFactory<>("locuriLibere"));

        tableViewRez.setRowFactory(tv -> new TableRow<Excursie>() {
            @Override
            protected void updateItem(Excursie excursie, boolean empty) {
                super.updateItem(excursie, empty);

                if (excursie == null || empty) {
                    // If the item is null or the row is empty, set the default style
                    setStyle("");
                } else {
                    try {
                        if (server.getFreeSeats(excursie.getId().intValue()) == 0) {
                            // If there are no available places, set the row style to red
                            setStyle("-fx-background-color: #ff0000;");
                        } else {
                            // Reset the row style
                            setStyle("");
                        }
                    } catch (SQLException | Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }
    public void initModelExcursii() throws Exception {
        Iterable<Excursie> excursii = server.getAllExcursii();
        List<Excursie> excursiiList = new ArrayList<>();
        for (Excursie excursie : excursii) {
            excursiiList.add(excursie);
        }
        modelExcursii.setAll(excursiiList);

    }
    public void setCombo1() {
        getOra1.getItems().removeAll();

        for (int i = 0; i <= 23; i++) {
            getOra1.getItems().add(i);
        }
    }
    private void setCombo2() {
        getOra1.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                // Clear previous items and populate the second ComboBox with numbers larger than the selected value
                getOra2.getItems().clear();
                for (int i = (int) newValue + 1; i <= 24; i++) {
                    getOra2.getItems().add(i);
                }
            }
        });
    }
    @FXML
    private void handleSearch(ActionEvent actionEvent) throws Exception {
        if (!getObiectiv.getText().isEmpty() && getOra1.getValue() != null && getOra2.getValue() != null) {
            modelExcursiiRezervari.clear();
            Iterable<Excursie> excursii = server.getExcursiiBetweenHours(getObiectiv.getText(), LocalTime.of((Integer)getOra1.getValue(), 0, 0).toString(), LocalTime.of((Integer) getOra2.getValue(), 0,0).toString());
            for (Excursie excursie : excursii) {
                System.out.println(excursie.getObiectivTuristic());
            }
            //modelExcursiiRezervari.setAll(StreamSupport.stream(server.getExcursiiBetweenHours(getObiectiv.getText(), LocalTime.of((Integer) getOra1.getValue(), 0), LocalTime.of((Integer) getOra2.getValue(), 0)).spliterator(),
              //      false).collect(Collectors.toList()));
            modelExcursiiRezervari.setAll(StreamSupport.stream(excursii.spliterator(),
                    false).collect(Collectors.toList()));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occured");
            alert.setContentText("Nu ati completat toate campurile!");
            alert.showAndWait();
        }
    }
    @FXML
    public void handleRezervare(ActionEvent actionEvent) {
        if (getNume.getText().isEmpty() || getTelefon.getText().isEmpty() || getLocuri.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occured");
            alert.setContentText("All fields must be filled!");
            alert.showAndWait();
            return;
        }
        if (tableViewRez.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occured");
            alert.setContentText("Selecteaza o excursie!");
            alert.showAndWait();
            return;
        }
        Excursie excursie = tableViewRez.getSelectionModel().getSelectedItem();
        try {
            //System.out.println(serviceExcursie.getFreeSeats(excursie.getId().intValue()));
            if (Integer.parseInt(getLocuri.getText()) > server.getFreeSeats(excursie.getId().intValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error has occured");
                alert.setContentText("Nu exista destule locuri!");
                alert.showAndWait();
                return;
            }
            server.addRezervare(agentie.getId(),excursie.getId(), getNume.getText(), getTelefon.getText(), Integer.parseInt(getLocuri.getText()));
            System.out.println(server.getFreeSeats(excursie.getId().intValue()));
//            modelExcursiiRezervari.clear();
//            modelExcursiiRezervari.setAll(StreamSupport.stream(server.getExcursiiBetweenHours(getObiectiv.getText(), LocalTime.of((Integer) getOra1.getValue(), 0), LocalTime.of((Integer) getOra2.getValue(), 0)).spliterator(),
//                    false).collect(Collectors.toList()));
            modelExcursii.clear();
            initModelExcursii();
            modelExcursiiRezervari.clear();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Success");
            alert.setContentText("Adaugare cu succes!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occured");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        btnLogout.getScene().getWindow().hide();
    }

    public void setServer(IServices server) throws Exception {
        this.server = server;
        //initModelExcursii();
        setCombo1();
        setCombo2();
    }

//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//
//    }

    @Override
    public void rezervationMade() throws Exception {
        Platform.runLater(()->{
                modelExcursiiRezervari.clear();
                modelExcursii.clear();
            try {
//                modelExcursiiRezervari.setAll(StreamSupport.stream(server.getExcursiiBetweenHours(getObiectiv.getText(), LocalTime.of((Integer) getOra1.getValue(), 0), LocalTime.of((Integer) getOra2.getValue(), 0)).spliterator(),
//                        false).collect(Collectors.toList()));
                modelExcursii.setAll(StreamSupport.stream(server.getAllExcursii().spliterator(),
                        false).collect(Collectors.toList()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void logout() {
        try {
            server.logout(agentie, this);
            btnLogout.getScene().getWindow().hide();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setUser(Agentie agentie) throws Exception {
        this.agentie = agentie;
        //initialize();
        initModelExcursii();
    }
}