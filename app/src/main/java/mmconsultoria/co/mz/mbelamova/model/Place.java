package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
    private double latitude;
    private double longitude;
    private String name;

    public Place() {
    }



    protected Place(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
    }


    public Place(PlaceBuilder builder) {
        setLatitude(builder.latitude);
        setLongitude(builder.longitude);
        setName(builder.name);
    }

    public static PlaceBuilder build(double latitude, double longitude) {
        return new PlaceBuilder(latitude, longitude);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public Place setName(String name) {
        this.name = name;
        return this;
    }

    public static class PlaceBuilder {
        private double latitude, longitude;
        private String name;

        public PlaceBuilder(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public PlaceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Place build() {
            return new Place(this);
        }
    }

    @Override
    public String toString() {
        return "Place{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (Double.compare(place.latitude, latitude) != 0) return false;
        return Double.compare(place.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
