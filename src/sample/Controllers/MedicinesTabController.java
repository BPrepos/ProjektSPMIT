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

    public MedicinesTabController() {
    }

    public void refreshOnClick(){
        medIdTF.setText("");
        nameTF.setText("");
        substanceTF.setText("");
        quantityTF.setText("");
        priceTF.setText("");
        posXTF.setText("");
        posYTF.setText("");
        showMedicines();
    }

    @FXML
    private void deleteRecord(){
        String query = "DELETE FROM LEK WHERE ID_LEK="+medIdTF.getText()+";";
        controller.executeQuery(query);
        showMedicines();
    }

    @FXML
    private void updateRecord(){
        String query = "UPDATE LEK SET NAZWA ='"+nameTF.getText()+"',SUBSTANCJA='"+substanceTF.getText()+"', ILOSC="+quantityTF.getText()+", CENA="+
                priceTF.getText()+", X="+posXTF.getText()+", Y="+posYTF.getText()+
                " where ID_LEK="+medIdTF.getText()+"";
        controller.executeQuery(query);
        showMedicines();
    }

    @FXML
    public void insertRecord(){
        String query = "INSERT INTO LEK (NAZWA, SUBSTANCJA, ILOSC, CENA, X, Y) VALUES" +
                "('"+nameTF.getText()+"','"+substanceTF.getText()+"',"+quantityTF.getText()+","+priceTF.getText()+","+
                posXTF.getText()+","+posYTF.getText()+");";
        controller.executeQuery(query);
        showMedicines();
    }

    @FXML
    private void handleMouseAction(){
        Medicine medicine = tvMedicines.getSelectionModel().getSelectedItem();
        medIdTF.setText(medicine.getId().toString());
        nameTF.setText(medicine.getName());
        substanceTF.setText(medicine.getSubstance());
        quantityTF.setText(medicine.getQuantity().toString());
        priceTF.setText(Double.toString(medicine.getPrice()));
        posXTF.setText(Integer.toString(medicine.getPosX()));
        posYTF.setText(Integer.toString(medicine.getPosY()));
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
