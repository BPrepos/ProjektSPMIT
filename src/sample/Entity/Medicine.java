package sample.Entity;

public class Medicine {

private Long id;
private String name;
private String substance;
private Integer quantity;
private double price;
private Integer posX;
private Integer posY;


    public Medicine(Long id, String name, String substance, Integer quantity, double price, Integer posX, Integer posY) {
        this.id = id;
        this.name = name;
        this.substance = substance;
        this.quantity = quantity;
        this.price = price;
        this.posX = posX;
        this.posY = posY;
    }

    //constructor without id
    public Medicine(String name, String substance, Integer quantity, double price, Integer posX, Integer posY) {
        this.name = name;
        this.substance = substance;
        this.quantity = quantity;
        this.price = price;
        this.posX = posX;
        this.posY = posY;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getPosX() {
        return posX;
    }

    public void setPosX(Integer posX) {
        this.posX = posX;
    }

    public Integer getPosY() {
        return posY;
    }

    public void setPosY(Integer posY) {
        this.posY = posY;
    }


    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", substance='" + substance + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
