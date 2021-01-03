package sample.Entity;

public class MedicinePrescription extends Medicine {
    private Integer quantityToBuy;

    public MedicinePrescription(Long id, String name, String substance, Integer quantity, double price, Integer posX, Integer posY, Integer quantityToBuy) {
        super(id, name, substance, quantity, price, posX, posY);
        this.quantityToBuy = quantityToBuy;
    }

    public Integer getQuantityToBuy() {
        return quantityToBuy;
    }

    public void setQuantityToBuy(Integer quantityToBuy) {
        this.quantityToBuy = quantityToBuy;
    }
}
