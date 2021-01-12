package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.Algorithm.AlgorithmWrapper;
import sample.Algorithm.Position;
import sample.Entity.Client;
import sample.Entity.Medicine;
import sample.Entity.MedicinePrescription;
import sample.Entity.Prescription;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
    @FXML
    private ImageView logoImageView;


    //list of prescribed medicines
    ObservableList<MedicinePrescription> prescribedMeds;
    ObservableList<Medicine> allMeds;
    //final list of meds to algorithm
    ObservableList<Medicine> finalMeds = FXCollections.observableArrayList();
    //variable for retrieving medicine
    Medicine chosenSubstituteMedicine;
    MedicinePrescription receivedBackMedicinePrescription;


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

    public void finalizeOnClick(ActionEvent event){
        finalMeds.clear();
        ObservableList<MedicinePrescription> allInfoMedsList = getFinalizedAllInfoMeds();
        for(MedicinePrescription tmp : allInfoMedsList){
            finalMeds.add(new Medicine(tmp.getId(),tmp.getName(),tmp.getSubstance(),tmp.getQuantity(),tmp.getPrice(),tmp.getPosX(),tmp.getPosY()));
            String query = "update lek set ilosc="+(tmp.getQuantity()-tmp.getQuantityBought())+" where id_lek="+tmp.getId()+";";
            controller.executeQuery(query);
            if (tmp.getOldMedId() == -1) {
                if (tmp.getQuantityToBuy() == tmp.getQuantityBought()){
                    query = "delete from lek_recepta where id_recepta=" + tmp.getPrescriptionID() + " and id_lek=" + tmp.getId() + ";";
                }else{
                    int newQuantity = tmp.getQuantityToBuy() - tmp.getQuantityBought();
                    query = "update lek_recepta set ilosc=" + Integer.toString(newQuantity) + " where id_recepta=" + tmp.getPrescriptionID() + " and id_lek=" + tmp.getId();
                }
            }else{
                if (tmp.getQuantityToBuy() == tmp.getQuantityBought()){
                    query = "delete from lek_recepta where id_recepta=" + tmp.getPrescriptionID() + " and id_lek=" + tmp.getOldMedId() + ";";
                }else{
                    int newQuantity = tmp.getQuantityToBuy() - tmp.getQuantityBought();
                    query = "update lek_recepta set ilosc=" + Integer.toString(newQuantity) + " where id_recepta=" + tmp.getPrescriptionID() + " and id_lek=" + tmp.getOldMedId();
                }
            }
            controller.executeQuery(query);
        }
        for(Medicine finalMed : finalMeds){
            System.out.println(finalMed);
        }
        showPrescribedMedicines();
        AlgorithmWrapper algorithmWrapper = new AlgorithmWrapper();
        List<Position> positionList = algorithmWrapper.calculate(finalMeds, 1, 1);
        allMeds = getMedicineList();
        int x,y;
        double xd, yd;
        Pane root = new Pane();
        for (Medicine temp : allMeds)
        {
            x = temp.getPosX();
            y = temp.getPosY();
            Rectangle rectangle = new Rectangle(50*(x-1), 50*(y-1),50,50);
            rectangle.setFill(Color.WHITE);
            rectangle.setStroke(Color.BLACK);
            root.getChildren().add(rectangle);
        }
        Polyline polyline = new Polyline();
        for (Position temp2 : positionList)
        {
            xd = temp2.getX()*50.0-25.0;
            yd = temp2.getY()*50.0-25.0;
            polyline.getPoints().add(xd);
            polyline.getPoints().add(yd);
        }
        xd = 25.0;
        yd = 25.0;
        polyline.getPoints().add(xd);
        polyline.getPoints().add(yd);
        polyline.setStroke(Color.RED);
        root.getChildren().add(polyline);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Path plot");
        stage.show();
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



    public void loadSubstituteWindow(MedicinePrescription med){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/substitute.fxml"));
            Parent substituteWindowParent = loader.load();

            Scene substituteScene = new Scene(substituteWindowParent);

            SubstituteController substituteController = loader.getController();
            substituteController.initData(med,controller.getMedicinesTabController().getMedicineList());

            Stage stage = new Stage();
            stage.setScene(substituteScene);
            stage.setTitle("Substitute medicines");
            stage.showAndWait();
            if(substituteController.sendBackMed != null) {
                chosenSubstituteMedicine = substituteController.sendBackMed;
                receivedBackMedicinePrescription = substituteController.medSendToSubstitute;
            }
            for(int i=0; i<prescribedMeds.size(); i++){
                if(prescribedMeds.get(i).getId() == receivedBackMedicinePrescription.getId()){
                    prescribedMeds.get(i).setOldMedId(prescribedMeds.get(i).getId());
                    prescribedMeds.get(i).setId(chosenSubstituteMedicine.getId());
                    prescribedMeds.get(i).setName(chosenSubstituteMedicine.getName());
                    prescribedMeds.get(i).setSubstance(chosenSubstituteMedicine.getSubstance());
                    prescribedMeds.get(i).setQuantity(chosenSubstituteMedicine.getQuantity());
                    prescribedMeds.get(i).setPrice(chosenSubstituteMedicine.getPrice());
                    prescribedMeds.get(i).setPosX(chosenSubstituteMedicine.getPosX());
                    prescribedMeds.get(i).setPosY(chosenSubstituteMedicine.getPosY());
                    prescribedMeds.get(i).isAvaliable();
                }
            }
            tvPrescribedMedicines.setItems(prescribedMeds);
            tvPrescribedMedicines.refresh();

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
                loadSubstituteWindow(med);


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

    public ObservableList<Medicine> getMedicineList(){
        ObservableList<Medicine> medList = FXCollections.observableArrayList();
        Connection conn = controller.getConnection();
        String query = "SELECT lek.id_lek, lek.nazwa, lek.substancja, lek.ilosc, lek.cena, lek.x, lek.y " +
                "FROM lek";
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