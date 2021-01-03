package sample.Entity;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class MedicinePrescription extends Medicine {
    private Integer quantityToBuy;
    private TextField selectedTF;
    private Integer quantityBought = 0;
    private Long prescriptionID;

    public MedicinePrescription(Long id, String name, String substance, Integer quantity, double price, Integer posX, Integer posY, Integer quantityToBuy) {
        super(id, name, substance, quantity, price, posX, posY);
        this.quantityToBuy = quantityToBuy;
        this.selectedTF = new TextField("0");
    }

    public boolean isAvaliable(){
        if(getQuantity()==0){
            selectedTF.setDisable(true);
            return false;
        }
        return true;
    }

    public Integer getQuantityToBuy() {
        return quantityToBuy;
    }

    public void setQuantityToBuy(Integer quantityToBuy) {
        this.quantityToBuy = quantityToBuy;
    }

    public TextField getSelectedTF() {
        return selectedTF;
    }

    public void setSelectedTF(TextField selectedTF) {
        this.selectedTF = selectedTF;
    }

    @Override
    public String toString() {
        return super.toString() + " quantityToBuy=" + quantityToBuy;
    }

    public Integer getQuantityBought() {
        return quantityBought;
    }

    public void setQuantityBought(Integer quantityBought) {
        this.quantityBought = quantityBought;
    }

    public Long getPrescriptionID() {
        return prescriptionID;
    }

    public void setPrescriptionID(Long prescriptionID) {
        this.prescriptionID = prescriptionID;
    }
}
