module lab1.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.apache.logging.log4j;

    opens ro.mpp2024 to javafx.fxml;
    exports ro.mpp2024;

    opens ro.mpp2024.domain to javafx.fxml;
    exports ro.mpp2024.domain;

    opens ro.mpp2024.service to javafx.fxml;
    exports ro.mpp2024.service;

    opens ro.mpp2024.controller to javafx.fxml;
    exports ro.mpp2024.controller;

}