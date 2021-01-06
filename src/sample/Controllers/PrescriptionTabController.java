package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.w3c.dom.Node;
import sample.Entity.Client;
import sample.Entity.Medicine;
import sample.Entity.MedicinePrescription;
import sample.Entity.Prescription;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;

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
    private Button finalizeButton;
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
    @FXML
    private TableColumn<MedicinePrescription, TextField> colSelect;
    @FXML
    private TableColumn<MedicinePrescription, Button> colSubstitute;

    //substitute window
    @FXML
    private Button confirmBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TableView<Medicine> tvSubstituteMeds;
    @FXML
    private TableColumn<Medicine, Long> colMedSubId;
    @FXML
    private TableColumn<Medicine, String> colSubName;
    @FXML
    private TableColumn<Medicine, String> colSubSubstance;
    @FXML
    private TableColumn<Medicine, Integer> colSubQuantity;
    @FXML
    private TableColumn<Medicine, Double> colSubPrice;

    //list of prescribed medicines
    ObservableList<MedicinePrescription> prescribedMeds;
    //final list of meds to algorithm
    ObservableList<Medicine> finalMeds = FXCollections.observableArrayList();


    //controllers
    private Controller controller;

    public void injectController(Controller controller){
        this.controller = controller;
    }



    public void setSubstituteTableView(ObservableList<Medicine> list){
        colMedSubId.setCellValueFactory(new PropertyValueFactory<Medicine,Long>("id"));
        colSubName.setCellValueFactory(new PropertyValueFactory<Medicine,String>("name"));
        colSubSubstance.setCellValueFactory(new PropertyValueFactory<Medicine,String>("substance"));
        colSubQuantity.setCellValueFactory(new PropertyValueFactory<Medicine,Integer>("quantity"));
        colSubPrice.setCellValueFactory(new PropertyValueFactory<Medicine,Double>("price"));
        tvSubstituteMeds.setItems(list);
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

    public void finalizeOnClick(ActionEvent event){
       finalMeds.clear();
       ObservableList<MedicinePrescription> allInfoMedsList = getFinalizedAllInfoMeds();
       for(MedicinePrescription tmp : allInfoMedsList){
           finalMeds.add(new Medicine(tmp.getId(),tmp.getName(),tmp.getSubstance(),tmp.getQuantity(),tmp.getPrice(),tmp.getPosX(),tmp.getPosY()));
           String query = "update lek set ilosc="+(tmp.getQuantity()-tmp.getQuantityBought())+" where id_lek="+tmp.getId()+";";
           controller.executeQuery(query);
           if(tmp.getQuantityToBuy()==tmp.getQuantityBought()){
               query="delete from lek_recepta where id_recepta="+tmp.getPrescriptionID()+" and id_lek="+tmp.getId()+";";
           }else{
               int newQuantity = tmp.getQuantityToBuy()-tmp.getQuantityBought();
               query = "update lek_recepta set ilosc="+Integer.toString(newQuantity)+" where id_recepta="+tmp.getPrescriptionID()+" and id_lek="+tmp.getId();
           }
           controller.executeQuery(query);
       }
       for(Medicine finalMed : finalMeds){
            System.out.println(finalMed);
       }
       showPrescribedMedicines();
    }


    public void cancelOnClick(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }


    public ObservableList<MedicinePrescription> getFinalizedAllInfoMeds(){
        ObservableList<MedicinePrescription> tmpFinalMeds = FXCollections.observableArrayList();
        for(MedicinePrescription m : prescribedMeds){
            if(Pattern.matches("^[1-9][0-9]*$", m.getSelectedTF().getText()) == true ){
                m.setQuantityBought(Integer.parseInt(m.getSelectedTF().getText()));
                tmpFinalMeds.add(m);
            }
        }
       return tmpFinalMeds;
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

    public void confirmSubstituteOnCLick(ActionEvent event){
        Medicine substituteMedicine = tvSubstituteMeds.getSelectionModel().getSelectedItem();
        System.out.println(substituteMedicine);

    }

    public void loadSubstituteWindow(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/substitute.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root,600,400));
            stage.setTitle("Substitute medicines");
            stage.show();
            PrescriptionTabController prescriptionTabController = fxmlLoader.getController();
            ObservableList<Medicine> list = controller.getMedicinesTabController().getMedicineList();
            prescriptionTabController.setSubstituteTableView(list);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void showPrescribedMedicines(){
        Prescription prescription = getPrescription(peselTF.getText(),Integer.parseInt(codeTF.getText()));
        prescribedMeds = getPrescribedMedicineList(prescription);
        for(MedicinePrescription med : prescribedMeds){
            med.getSubstituteBtn().setOnAction(actionEvent -> {
               loadSubstituteWindow();


            });

        }
        colId.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Long>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,String>("name"));
        colSubstance.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,String>("substance"));
        colQuantityAvaliable.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Double>("price"));
        colPosX.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("posX"));
        colPosY.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("posY"));
        colQuantityPrescribed.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Integer>("quantityToBuy"));
        colSelect.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,TextField>("selectedTF"));
        colSubstitute.setCellValueFactory(new PropertyValueFactory<MedicinePrescription,Button>("substituteBtn"));
        tvPrescribedMedicines.setItems(prescribedMeds);

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
                med.isAvaliable();
                med.setPrescriptionID(pres.getId());
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
