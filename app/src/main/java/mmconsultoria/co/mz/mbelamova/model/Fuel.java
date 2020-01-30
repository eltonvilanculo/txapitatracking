package mmconsultoria.co.mz.mbelamova.model;

public class Fuel {
    private String name;
    private double pricePerLiter;
    private int code;

    public Fuel() {
    }

    public Fuel(int code, String name, double pricePerLiter) {
        this.code = code;
        this.name = name;
        this.pricePerLiter = pricePerLiter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(double pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
