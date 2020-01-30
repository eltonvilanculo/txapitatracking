package mmconsultoria.co.mz.mbelamova.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

import androidx.annotation.Nullable;

public class Ride {
    private String vehicleLicencePlate;
    private double price;
    private double distance;
    private long startTime;
    private String id;
    private Map<String, Trip> trips;
    private String trajectory;
    private DriverLicence driverLicence;
    private boolean running;
    private TripStatus status = TripStatus.AVAILABLE;
    private String driverId;
    private String driverName;
    private String driverPhoto;
    private int vehicleColor;
    private String vehicleFuel;
    private boolean maleGender;
    private float driverRating;
    private String driverNotifyId;
    private int numberOfSeats;
    private double literPerKilometer;

    public Ride() {
    }

    private Ride(RideBuilder builder) {
        id = builder.id;
        trips = builder.trips;
        driverLicence = builder.person.getDriverData().getLicence();
        vehicleColor = builder.person.getDriverData().getVehicle().getColor();
        vehicleFuel = builder.person.getDriverData().getVehicle().getFuelType();
        vehicleLicencePlate = builder.person.getDriverData().getVehicle().getLicencePlate();
        literPerKilometer = builder.person.getDriverData().getVehicle().getNumberOfCylinders() == 0 ?  2/2000 : builder.person.getDriverData().getVehicle().getNumberOfCylinders();
        driverId = builder.person.getId();
        driverRating = builder.person.getReview() == null || builder.person.getReview().getDriver() == null ? 0 : builder.person.getReview().driverResume();
        driverName = builder.person.retrieveFullName();
        driverPhoto = builder.person.getPhotoUri();
        trajectory = builder.trajectory;
        price = builder.price;
        startTime = builder.startTime;
        numberOfSeats = builder.numberOfSeats;
    }

    public static RideBuilder builder(Person person, String trajectory,int numberOfSeats, long startTime) {
        return new RideBuilder(person, trajectory, numberOfSeats,startTime);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Trip> getTrips() {
        return trips;
    }


    public void setTrips(Map<String, Trip> trips) {
        this.trips = trips;
    }

    public DriverLicence getDriverLicence() {
        return driverLicence;
    }

    public void setDriverLicence(DriverLicence driverLicence) {
        this.driverLicence = driverLicence;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
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

    public String getDriverPhoto() {
        return driverPhoto;
    }

    public void setDriverPhoto(String driverPhoto) {
        this.driverPhoto = driverPhoto;
    }

    public int getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(int vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getDriverNotifyId() {
        return driverNotifyId;
    }

    public void setDriverNotifyId(String driverNotifyId) {
        this.driverNotifyId = driverNotifyId;
    }

    public String getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(String trajectory) {
        this.trajectory = trajectory;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public boolean isMaleGender() {
        return maleGender;
    }

    public void setMaleGender(boolean maleGender) {
        this.maleGender = maleGender;
    }

    public String getVehicleLicencePlate() {
        return vehicleLicencePlate;
    }

    public void setVehicleLicencePlate(String vehicleLicencePlate) {
        this.vehicleLicencePlate = vehicleLicencePlate;
    }

    public String getVehicleFuel() {
        return vehicleFuel;
    }

    public void setVehicleFuel(String vehicleFuel) {
        this.vehicleFuel = vehicleFuel;
    }

    public void setDriverRating(float driverRating) {
        this.driverRating = driverRating;
    }

    public float getDriverRating() {
        return driverRating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    @IgnoreExtraProperties
    public static class RideBuilder {
        private String trajectory;
        private double price;
        private String id;
        private Person person;
        private final long startTime;
        private Map<String, Trip> trips;
        private int numberOfSeats;


        public RideBuilder(Person person, String trajectory,int numberOfSeats, long startTime) {
            this.person = person;
            this.trajectory = trajectory;
            this.numberOfSeats = numberOfSeats;
            this.startTime = startTime;
        }

        public RideBuilder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        public RideBuilder trips(@Nullable Map<String, Trip> trips) {
            this.trips = trips;
            return this;
        }

        public RideBuilder price(double price) {
            this.price = price;
            return this;
        }


        public Ride build() {
            return new Ride(this);
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ride ride = (Ride) o;

        return id.equals(ride.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Ride{" +
                "price=" + price +
                ", startTime=" + startTime +
                ", id='" + id + '\'' +
                ", trips=" + trips +
                ", trajectory='" + trajectory + '\'' +
                ", driverLicence=" + driverLicence +
                ", running=" + running +
                ", status=" + status +
                ", driverId='" + driverId + '\'' +
                ", driverName='" + driverName + '\'' +
                ", driverPhoto='" + driverPhoto + '\'' +
                ", vehicleColor='" + vehicleColor + '\'' +
                ", maleGender=" + maleGender +
                ", driverRating='" + driverRating + '\'' +
                ", driverNotifyId='" + driverNotifyId + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                '}';
    }

    public double getLiterPerKilometer() {
        return literPerKilometer;
    }

    public void setLiterPerKilometer(double literPerKilometer) {
        this.literPerKilometer = literPerKilometer;
    }
}
