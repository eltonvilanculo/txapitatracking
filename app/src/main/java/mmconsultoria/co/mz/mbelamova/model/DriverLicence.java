package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverLicence implements Parcelable {

    private String number;
    /**
     * licence type exemple : ligeiro
     *
     * */
    private String type;
    /**
     * licence level exemple : A1
     *
     * */
    private String licenceLevel;

    public DriverLicence(){

    }

    public DriverLicence(String number, String type, String level) {
        this.number = number;
        this.type = type;
        this.licenceLevel = level;
    }

    protected DriverLicence(Parcel in) {
        number = in.readString();
        type = in.readString();
        licenceLevel = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(type);
        dest.writeString(licenceLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverLicence> CREATOR = new Creator<DriverLicence>() {
        @Override
        public DriverLicence createFromParcel(Parcel in) {
            return new DriverLicence(in);
        }

        @Override
        public DriverLicence[] newArray(int size) {
            return new DriverLicence[size];
        }
    };

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicenceLevel() {
        return licenceLevel;
    }

    public void setLicenceLevel(String licenceLevel) {
        this.licenceLevel = licenceLevel;
    }


    @Override
    public String toString() {
        return "DriverLicence{" +
                "number='" + number + '\'' +
                ", type='" + type + '\'' +
                ", licenceLevel='" + licenceLevel + '\'' +
                '}';
    }
}
