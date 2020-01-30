package mmconsultoria.co.mz.mbelamova.model;

public class VehicleExtraData {
    private double cylinder;
    private double fuelPerKilometer;


    public VehicleExtraData(double cylinder, double fuelPerKilometer) {
        this.cylinder = cylinder;
        this.fuelPerKilometer = fuelPerKilometer;
    }

    public double getCylinder() {
        return cylinder;
    }

    public void setCylinder(double cylinder) {
        this.cylinder = cylinder;
    }

    public double getFuelPerKilometer() {
        return fuelPerKilometer;
    }

    public void setFuelPerKilometer(double fuelPerKilometer) {
        this.fuelPerKilometer = fuelPerKilometer;
    }

    @Override
    public String toString() {
        return "VehicleExtraData{" +
                "cylinder=" + cylinder +
                ", fuelPerKilometer=" + fuelPerKilometer +
                '}';
    }
}
