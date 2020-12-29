module PharmacyApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens sample;
    opens sample.Controllers;
    opens sample.Entity;
}