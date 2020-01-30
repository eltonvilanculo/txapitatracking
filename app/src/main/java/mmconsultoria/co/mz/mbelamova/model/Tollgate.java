package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tollgate implements Parcelable {
    private String id;
    private Place place;

    public Tollgate Create() {
        return new Tollgate();
    }

    private Tollgate() {
    }

    protected Tollgate(Parcel in) {
        id = in.readString();
        place = in.readParcelable(Place.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(place, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tollgate> CREATOR = new Creator<Tollgate>() {
        @Override
        public Tollgate createFromParcel(Parcel in) {
            return new Tollgate(in);
        }

        @Override
        public Tollgate[] newArray(int size) {
            return new Tollgate[size];
        }
    };

    public Tollgate id(String id) {
        this.id = id;
        return this;
    }

    public Tollgate name(String name) {
        if (place != null) {
            place.setName(name);

        } else {
            place = new Place();
            place.setName(name);
        }
        return this;
    }

    public Tollgate latitude(double latitude) {
        if (place != null) {
            place.setLatitude(latitude);

        } else {
            place = new Place();
            place.setLatitude(latitude);
        }
        return this;
    }


    public Tollgate longetude(double longitude) {
        if (place != null) {
            place.setLatitude(longitude);

        } else {
            place = new Place();
            place.setLongitude(longitude);
        }
        return this;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return place.getName();
    }

    public double getLatitude() {
        return place.getLatitude();
    }

    public double getLongitude() {
        return place.getLongitude();
    }
}
