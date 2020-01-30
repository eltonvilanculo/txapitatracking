package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Vehicle implements Parcelable, MultipleMediaSource {
    private String maker;
    private String model;
    private int color;
    private int year;
    private int lot;
    private String type;
    private String licencePlate;
    private double fuelVolumePerLiter;


    private List<String> images;
    private double numberOfCylinders;
    private String fuelType;

    public Vehicle() {
    }

    public Vehicle(String maker, String model, int color, int year, int lot, String type, String licencePlate, List<String> images, double numberOfCylinders) {
        this.maker = maker;
        this.model = model;
        this.color = color;
        this.year = year;
        this.lot = lot;
        this.type = type;
        this.licencePlate = licencePlate;
        this.images = images;
        this.numberOfCylinders = numberOfCylinders;
    }

    protected Vehicle(Parcel in) {
        maker = in.readString();
        model = in.readString();
        color = in.readInt();
        year = in.readInt();
        lot = in.readInt();
        type = in.readString();
        licencePlate = in.readString();
        fuelVolumePerLiter = in.readDouble();
        images = in.createStringArrayList();
        numberOfCylinders = in.readDouble();
        fuelType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maker);
        dest.writeString(model);
        dest.writeInt(color);
        dest.writeInt(year);
        dest.writeInt(lot);
        dest.writeString(type);
        dest.writeString(licencePlate);
        dest.writeDouble(fuelVolumePerLiter);
        dest.writeStringList(images);
        dest.writeDouble(numberOfCylinders);
        dest.writeString(fuelType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getLot() {
        return lot;
    }

    public void setLot(int lot) {
        this.lot = lot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }


    public double getNumberOfCylinders() {
        return numberOfCylinders;
    }

    public void setNumberOfCylinders(double numberOfCylinders) {
        this.numberOfCylinders = numberOfCylinders;
    }

    @Override
    public List<String> getDataUris() {
        return images;
    }

    @Override
    public void setDataUris(List<String> images) {
        this.images = images;
    }

    public double getFuelVolumePerLiter() {
        return fuelVolumePerLiter;
    }

    public void setFuelVolumePerLiter(double fuelVolumePerLiter) {
        this.fuelVolumePerLiter = fuelVolumePerLiter;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "maker='" + maker + '\'' +
                ", model='" + model + '\'' +
                ", color=" + color +
                ", year=" + year +
                ", lot=" + lot +
                ", type='" + type + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", fuelVolumePerLiter=" + fuelVolumePerLiter +
                ", images=" + images +
                ", numberOfCylinders=" + numberOfCylinders +
                ", fuelType='" + fuelType + '\'' +
                '}';
    }
}
