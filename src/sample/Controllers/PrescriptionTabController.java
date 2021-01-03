package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Entity.Client;
import sample.Entity.Medicine;
import sample.Entity.MedicinePrescription;
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
    @FXML
    private TableView<MedicinePrescription> tvPrescribedMedicines;
    @FXML
    private TableColumn<MedicinePrescription, Long> colId;
    @FXML
    private TableColumn<MedicinePrescription, String> colName;
    @FXML
    private TableColumn<MedicinePrescription, String> colSubstance;
    @FXML
    private TableColumn<MedicinePrescription, Integer> colQuantityAvaliable;
    @FXML
    private TableColumn<MedicinePrescription, Double> colPrice;
    @FXML
    private TableColumn<MedicinePrescription, Integer> colPosX;
    @FXML
    private TableColumn<MedicinePrescription, Integer> colPosY;
    @FXML
    private TableColumn<MedicinePrescription, Integer> colQuantityPrescribed;

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
            showPrescribedMedicines();
        }
    }

    public void refreshOnClick(){
        peselDetailsTF.setText("");
        nameDetailsTF.setText("");
        surnameDetailsTF.setText("");
        otherPrescriptionTA.setText("");
        peselTF.setText("");
        codeTF.setText("");
        tvPrescribedMedicines.getItems().clear();
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

    public void showPrescribedMedicines(){
        Prescription prescription = getPrescription(peselTF.getText(),Integer.parseInt(codeTF.getText()));
        ObservableList<MedicinePrescription> medList = getPrescribedMedicineList(prescription);
        colId.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Long>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,String>("name"));
        colSubstance.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,String>("substance"));
        colQuantityAvaliable.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Double>("price"));
        colPosX.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("posX"));
        colPosY.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("posY"));
        colQuantityPrescribed.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("quantityToBuy"));
        tvPrescribedMedicines.setItems(medList);
    }

    public ObservableList<MedicinePrescription> getPrescribedMedicineList(Prescription pres){
        ObservableList<MedicinePrescription> medList = FXCollections.observableArrayList();
        Connection conn = controller.getConnection();
        String query = "SELECT lek.id_lek, lek.nazwa, lek.substancja, lek.ilosc, lek.cena, lek.x, lek.y,lek_recepta.ilosc as ilosc_przepisana " +
                "FROM lek_recepta " +
                "INNER JOIN lek ON lek_recepta.id_lek=lek.id_lek " +
                "where id_recepta="+pres.getId();
        Statement st;
        ResultSet rs;
        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            MedicinePrescription med;
            while (rs.next()){
                med = new MedicinePrescription(rs.getLong("ID_LEK"),rs.getString("NAZWA"),rs.getString("SUBSTANCJA"),rs.getInt("ILOSC"),rs.getDouble("CENA"),
                        rs.getInt("X"),rs.getInt("Y"), rs.getInt("ilosc_przepisana"));
                medList.add(med);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return medList;
    }

    public Prescription getPrescription(String pesel, Integer code){
        Connection conn = controller.getConnection();
        String query = "SELECT * FROM RECEPTA WHERE PESEL='"+pesel+"' AND KOD="+code+";";
        Statement st;
        ResultSet rs;
        Prescription pres = new Prescription();
        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()){
                pres = new Prescription(rs.getLong("ID_RECEPTA"),rs.getString("PESEL"),rs.getInt("KOD"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return pres;
    }

    

}
