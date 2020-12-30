package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.Entity.Client;
import sample.Entity.Medicine;
import sample.Entity.Prescription;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PrescriptionTabController {


    @FXML
    private TextField codeTF;
    @FXML
    private TextField peselTF;
    @FXML
    private Button checkButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField peselDetailsTF;
    @FXML
    private TextField nameDetailsTF;
    @FXML
    private TextField surnameDetailsTF;
    @FXML
    private TextArea otherPrescriptionTA;

    //controllers
    private Controller controller;

    public void injectController(Controller controller){
        this.controller = controller;
    }




    public void checkPrescription(){
        ObservableList<Client> clientDetails = getClientList();
        if(clientDetails.size()==0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Client not found!");
            alert.showAndWait();
        }else if(clientDetails.size()>=2){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Too many matches found!");
            alert.showAndWait();
        }else{
            peselDetailsTF.setText(clientDetails.get(0).getPesel());
            nameDetailsTF.setText(clientDetails.get(0).getName());
            surnameDetailsTF.setText(clientDetails.get(0).getSurname());
            ObservableList<Prescription> otherPrescriptions = getOtherPrescriptions();
            String prescriptionResult = "";
            for (Prescription pre : otherPrescriptions){
                prescriptionResult += pre.getCode()+"\n";
            }
            otherPrescriptionTA.setText(prescriptionResult);
        }
    }

    public void refreshOnClick(){
        peselDetailsTF.setText("");
        nameDetailsTF.setText("");
        surnameDetailsTF.setText("");
        otherPrescriptionTA.setText("");
        peselTF.setText("");
        codeTF.setText("");
    }

    public ObservableList<Client> getClientList(){
        ObservableList<Client> clientList = FXCollections.observableArrayList();
        Connection conn = controller.getConnection();
        String query = "SELECT * FROM KLIENT WHERE PESEL='"+peselTF.getText()+"';";
        Statement st;
        ResultSet rs;
        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Client client;
            while (rs.next()){
                client = new Client(rs.getString("PESEL"),rs.getString("IMIE"),rs.getString("NAZWISKO"));
                clientList.add(client);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return clientList;
    }

    public ObservableList<Prescription> getOtherPrescriptions(){
        ObservableList<Prescription> prescriptionList = FXCollections.observableArrayList();
        Connection conn = controller.getConnection();
        String query = "SELECT * FROM RECEPTA WHERE PESEL='"+peselTF.getText()+"';";
        Statement st;
        ResultSet rs;
        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Prescription prescription;
            while (rs.next()){
                prescription = new Prescription(rs.getString("PESEL"),rs.getInt("KOD"));
                prescriptionList.add(prescription);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return prescriptionList;
    }

    public ObservableList<Medicine> getPrescribedMedicineList(){
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

    

}
