package sample.Entity;

public class Prescription {
    private Long id;
    private String pesel;
    private Integer code;

    public Prescription(Long id, String pesel, Integer code) {
        this.id = id;
        this.pesel = pesel;
        this.code = code;
    }

    //constructor without id
    public Prescription(String pesel, Integer code) {
        this.pesel = pesel;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
