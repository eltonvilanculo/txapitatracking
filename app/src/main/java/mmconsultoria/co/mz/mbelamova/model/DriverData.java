package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverData implements Parcelable {
    private Vehicle vehicle;
    private DriverLicence licence;
    private boolean enabled;

    public DriverData() {
    }

    public DriverData(Vehicle vehicle, DriverLicence licence) {
        this.vehicle = vehicle;
        this.licence = licence;
    }

    protected DriverData(Parcel in) {
        vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        licence = in.readParcelable(DriverLicence.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(vehicle, flags);
        dest.writeParcelable(licence, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverData> CREATOR = new Creator<DriverData>() {
        @Override
        public DriverData createFromParcel(Parcel in) {
            return new DriverData(in);
        }

        @Override
        public DriverData[] newArray(int size) {
            return new DriverData[size];
        }
    };

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DriverLicence getLicence() {
        return licence;
    }

    public void setLicence(DriverLicence licence) {
        this.licence = licence;
    }


    @Override
    public String toString() {
        return "DriverData{" +
                "vehicle=" + vehicle +
                ", licence=" + licence +
                '}';
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
