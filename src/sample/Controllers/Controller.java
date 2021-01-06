package sample.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Properties;


public class Controller {

    //injecting controllers
    @FXML
    private MedicinesTabController medicinesTabController;

    //getter for controller
    public MedicinesTabController getMedicinesTabController() {
        return medicinesTabController;
    }

    public void setMedicinesTabController(MedicinesTabController medicinesTabController) {
        this.medicinesTabController = medicinesTabController;
    }

    @FXML
    private PrescriptionTabController prescriptionTabController;

    public void initialize() {
        medicinesTabController.injectController(this);
        prescriptionTabController.injectController(this);
    }


    public Connection getConnection(){
        Connection conn;
        try{
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost/postgres";
            Properties props = new Properties();
            props.setProperty("user","postgres");
            props.setProperty("password","admin");
            conn = DriverManager.getConnection(url, props);
            return conn;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try{
            st = conn.createStatement();
            st.executeUpdate(query);
            st.executeUpdate("commit");
        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
