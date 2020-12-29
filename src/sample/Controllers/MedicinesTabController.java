package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Entity.Medicine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MedicinesTabController {

    @FXML
    Button insertButton;
    @FXML
    Button deleteButton;
    @FXML
    Button updateButton;
    @FXML
    Button refreshButton;
    @FXML
    TextField medIdTF;
    @FXML
    TextField nameTF;
    @FXML
    TextField substanceTF;
    @FXML
    TextField quantityTF;
    @FXML
    TextField priceTF;
    @FXML
    TextField posXTF;
    @FXML
    TextField posYTF;
    @FXML
    private TableView<Medicine> tvMedicines;
    @FXML
    private TableColumn<Medicine,Long> medIdCol;
    @FXML
    private TableColumn<Medicine,String> nameCol;
    @FXML
    private TableColumn<Medicine,String> substanceCol;
    @FXML
    private TableColumn<Medicine,Integer> quantityCol;
    @FXML
    private TableColumn<Medicine,Double> priceCol;
    @FXML
    private TableColumn<Medicine,Integer> posXCol;
    @FXML
    private TableColumn<Medicine,Integer> posYCol;

    //controllers
    private Controller controller;

    public void injectController(Controller controller){
        this.controller = controller;
    }

    public void refreshOnClick(){
        showMedicines();
    }

    public ObservableList<Medicine> getMedicineList(){
        ObservableList<Medicine> medList = FXCollections.observableArrayList();
        Connection conn = controller.getConnection();
        String query = "SELECT * FROM LEK";
        Statement st;
        ResultSet rs;
        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Medicine med;
            while (rs.next()){
                med = new Medicine(rs.getLong("ID_LEK"),rs.getString("NAZWA"),rs.getString("SUBSTANCJA"),rs.getInt("ILOSC"),rs.getDouble("CENA"),
                        rs.getInt("X"),rs.getInt("Y"));
                medList.add(med);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return medList;
    }

    public void showMedicines(){
        ObservableList<Medicine> list = getMedicineList();
        medIdCol.setCellValueFactory(new PropertyValueFactory<Medicine,Long>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Medicine,String>("name"));
        substanceCol.setCellValueFactory(new PropertyValueFactory<Medicine,String>("substance"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<Medicine,Integer>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<Medicine,Double>("price"));
        posXCol.setCellValueFactory(new PropertyValueFactory<Medicine,Integer>("posX"));
        posYCol.setCellValueFactory(new PropertyValueFactory<Medicine,Integer>("posY"));
        tvMedicines.setItems(list);

    }




}
