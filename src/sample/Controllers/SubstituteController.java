package sample.Controllers;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.Entity.Medicine;
import sample.Entity.MedicinePrescription;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SubstituteController {


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


    public MedicinePrescription medSendToSubstitute;
    ObservableList<Medicine> substituteMeds;
    public Medicine sendBackMed;





    public void initData( MedicinePrescription medSendToSubstitute, ObservableList<Medicine> substituteMeds) {
        colMedSubId.setCellValueFactory(new PropertyValueFactory<Medicine,Long>("id"));
        colSubName.setCellValueFactory(new PropertyValueFactory<Medicine,String>("name"));
        colSubSubstance.setCellValueFactory(new PropertyValueFactory<Medicine,String>("substance"));
        colSubQuantity.setCellValueFactory(new PropertyValueFactory<Medicine,Integer>("quantity"));
        colSubPrice.setCellValueFactory(new PropertyValueFactory<Medicine,Double>("price"));
        tvSubstituteMeds.setItems(substituteMeds);
        this.medSendToSubstitute = medSendToSubstitute;
        this.substituteMeds = substituteMeds;
    }




    public void cancelOnClick(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void confirmSubstituteOnCLick(ActionEvent event) throws IOException {
        Medicine substituteMedicine = tvSubstituteMeds.getSelectionModel().getSelectedItem();
        System.out.println(substituteMedicine.toString());
        sendBackMed = substituteMedicine;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/prescriptionTab.fxml"));
        Parent prescriptionTabParent = loader.load();


        Stage stage = (Stage) confirmBtn.getScene().getWindow();
        stage.close();



    }

}
