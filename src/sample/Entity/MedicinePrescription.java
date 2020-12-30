package sample.Entity;

public class MedicinePrescription {

    private Long prescriptionId;
    private Long medicineId;
    private Integer quantity;


    public MedicinePrescription(Long prescriptionId, Long medicineId, Integer quantity) {
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.quantity = quantity;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
