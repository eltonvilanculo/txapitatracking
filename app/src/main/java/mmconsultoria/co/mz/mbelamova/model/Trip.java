package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

import androidx.annotation.Nullable;
import mmconsultoria.co.mz.mbelamova.activity.Location;

public class Trip implements Parcelable, Location {
    private String id;
    private String driverId;
    private String driverName;
    private double price;
    private String clientName;
    private String clientId;
    private long startTime, endTime;
    private List<Tollgate> listOfTollgates;
    private String trajectory;
    private Place startPoint;
    private Place endPoint;
    private double distanceInKilo;
    private boolean accepted;
    private TripStatus status;
    private boolean empty = false;
    private String rideId;
    private String changedBy = CHANGED_BY_CLIENT;
    private String driverNotificationId;
    private String clientNotificationId;


    protected Trip(Parcel in) {
        id = in.readString();
        driverId = in.readString();
        driverName = in.readString();
        price = in.readDouble();
        clientName = in.readString();
        clientId = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        listOfTollgates = in.createTypedArrayList(Tollgate.CREATOR);
        startPoint = in.readParcelable(Place.class.getClassLoader());
        trajectory = in.readString();
        endPoint = in.readParcelable(Place.class.getClassLoader());
        distanceInKilo = in.readDouble();
        accepted = in.readByte() != 0;
        empty = in.readByte() != 0;
        rideId = in.readString();
        changedBy = in.readString();
        driverNotificationId = in.readString();
        clientNotificationId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(driverId);
        dest.writeString(driverName);
        dest.writeDouble(price);
        dest.writeString(clientName);
        dest.writeString(clientId);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeTypedList(listOfTollgates);
        dest.writeParcelable(startPoint, flags);
        dest.writeString(trajectory);
        dest.writeParcelable(endPoint, flags);
        dest.writeDouble(distanceInKilo);
        dest.writeByte((byte) (accepted ? 1 : 0));
        dest.writeByte((byte) (empty ? 1 : 0));
        dest.writeString(rideId);
        dest.writeString(changedBy);
        dest.writeString(driverNotificationId);
        dest.writeString(clientNotificationId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public String getClientNotificationId() {
        return clientNotificationId;
    }

    public void setClientNotificationId(String clientNotificationId) {
        this.clientNotificationId = clientNotificationId;
    }

    public static final String CHANGED_BY_CLIENT = "CLIENT";
    public static final String CHANGED_BY_DRIVER = "DRIVER";


    public Trip() {
    }

    public static TripBuilder builder(Person client, Place startingPoint, Place destinationPoint) {
        return new TripBuilder(client, startingPoint, destinationPoint);
    }

    private Trip(TripBuilder builder) {
        clientName = builder.client.getName() + " " + builder.client.getLastName();
        clientId = builder.client.getId();
        clientNotificationId = builder.client.getNotificationId();
        price = builder.price;
        startTime = builder.startTime;
        endTime = builder.endTime;
        listOfTollgates = builder.listOfTollgates;
        startPoint = builder.startingPoint;
        endPoint = builder.destinationPoint;
        distanceInKilo = builder.distanceInKillo;
        driverName = builder.driverName;
        driverId = builder.driverId;
        status = builder.status;
    }

    public static Trip empty() {
        final Trip trip = new Trip();
        trip.empty = true;
        return trip;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<Tollgate> getListOfTollgates() {
        return listOfTollgates;
    }

    public void setListOfTollgates(List<Tollgate> listOfTollgates) {
        this.listOfTollgates = listOfTollgates;
    }

    public Place getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Place startPoint) {
        this.startPoint = startPoint;
    }

    public Place getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Place endPoint) {
        this.endPoint = endPoint;
    }

    public double getDistanceInKilo() {
        return distanceInKilo;
    }

    public void setDistanceInKilo(double distanceInKilo) {
        this.distanceInKilo = distanceInKilo;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public String name() {
        return clientName;
    }

    @Override
    public Place startPoint() {
        return getStartPoint();
    }

    @Override
    public Place endPoint() {
        return getEndPoint();
    }

    public boolean isEmpty() {
        return empty;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CharSequence getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getDriverNotificationId() {
        return driverNotificationId;
    }

    public void setDriverNotificationId(String driverNotificationId) {
        this.driverNotificationId = driverNotificationId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


    public String getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(String trajectory) {
        this.trajectory = trajectory;
    }

    @IgnoreExtraProperties
    public static class TripBuilder {
        public double price;
        private double distanceInKillo;
        private Place destinationPoint;
        private Person client;
        private long startTime;
        private long endTime;
        private List<Tollgate> listOfTollgates;
        private Place startingPoint;
        private String driverId;
        private String driverName;
        private TripStatus status = TripStatus.CLIENT_REQUESTED;


        public TripBuilder(Person client, Place startingPoint, Place destinationPoint) {
            this.client = client;
            this.startingPoint = startingPoint;
            this.destinationPoint = destinationPoint;
        }

        public TripBuilder tollgates(@Nullable List<Tollgate> tollgates) {
            this.listOfTollgates = tollgates;
            return this;
        }

        public TripBuilder price(double price) {
            this.price = price;
            return this;
        }

        public TripBuilder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public TripBuilder driverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public TripBuilder driverName(String driverName) {
            this.driverName = driverName;
            return this;
        }

        public TripBuilder distanceInKillo(double distance) {
            distanceInKillo = distance;
            return this;
        }

        public TripBuilder endTime(long endTime) {
            this.endTime = endTime;
            return this;
        }

        public TripBuilder status(TripStatus status) {
            this.status = status;
            return this;
        }


        public Trip build() {
            return new Trip(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip = (Trip) o;

        if (Double.compare(trip.price, price) != 0) return false;
        if (startTime != trip.startTime) return false;
        if (endTime != trip.endTime) return false;
        if (Double.compare(trip.distanceInKilo, distanceInKilo) != 0) return false;
        if (accepted != trip.accepted) return false;
        if (empty != trip.empty) return false;
        if (id != null ? !id.equals(trip.id) : trip.id != null) return false;
        if (driverId != null ? !driverId.equals(trip.driverId) : trip.driverId != null)
            return false;
        if (driverName != null ? !driverName.equals(trip.driverName) : trip.driverName != null)
            return false;
        if (clientName != null ? !clientName.equals(trip.clientName) : trip.clientName != null)
            return false;
        if (clientId != null ? !clientId.equals(trip.clientId) : trip.clientId != null)
            return false;
        if (listOfTollgates != null ? !listOfTollgates.equals(trip.listOfTollgates) : trip.listOfTollgates != null)
            return false;
        if (startPoint != null ? !startPoint.equals(trip.startPoint) : trip.startPoint != null)
            return false;
        if (endPoint != null ? !endPoint.equals(trip.endPoint) : trip.endPoint != null)
            return false;
        if (status != trip.status) return false;
        if (rideId != null ? !rideId.equals(trip.rideId) : trip.rideId != null) return false;
        if (changedBy != null ? !changedBy.equals(trip.changedBy) : trip.changedBy != null)
            return false;
        return driverNotificationId != null ? driverNotificationId.equals(trip.driverNotificationId) : trip.driverNotificationId == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (driverId != null ? driverId.hashCode() : 0);
        result = 31 * result + (driverName != null ? driverName.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (clientName != null ? clientName.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        result = 31 * result + (listOfTollgates != null ? listOfTollgates.hashCode() : 0);
        result = 31 * result + (startPoint != null ? startPoint.hashCode() : 0);
        result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
        temp = Double.doubleToLongBits(distanceInKilo);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (accepted ? 1 : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (empty ? 1 : 0);
        result = 31 * result + (rideId != null ? rideId.hashCode() : 0);
        result = 31 * result + (changedBy != null ? changedBy.hashCode() : 0);
        result = 31 * result + (driverNotificationId != null ? driverNotificationId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", driverName='" + driverName + '\'' +
                ", price=" + price +
                ", clientName='" + clientName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", listOfTollgates=" + listOfTollgates +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", distanceInKilo=" + distanceInKilo +
                ", accepted=" + accepted +
                ", status=" + status +
                ", empty=" + empty +
                ", rideId='" + rideId + '\'' +
                ", changedBy='" + changedBy + '\'' +
                ", driverNotificationId='" + driverNotificationId + '\'' +
                '}';
    }
}
