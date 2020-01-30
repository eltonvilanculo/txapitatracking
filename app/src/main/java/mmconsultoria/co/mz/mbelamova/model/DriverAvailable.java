package mmconsultoria.co.mz.mbelamova.model;

import androidx.annotation.NonNull;
import mmconsultoria.co.mz.mbelamova.activity.Location;

public class DriverAvailable implements Location {
    private String driverId, driverName, vehicleBrand, vehicleModel;
    private int color;
    private double rate;
    private Place actualLocation;
    private Place startingPoint;
    private Place endPoint;
    private boolean shuttedDown = false;
    private Fuel fuel;

    public DriverAvailable(DriverAvailableBuilder builder) {
        driverId = builder.driverId;
        driverName = builder.driverName;
        vehicleBrand = builder.vehicleBrand;
        vehicleModel = builder.vehicleModel;
        color = builder.color;
        rate = builder.rate;
        actualLocation = builder.actualLocation;
        startingPoint = builder.startingPoint;
        endPoint = builder.endPoint;
        shuttedDown = builder.shuttedDown;
        fuel = builder.fuel;

    }

    public DriverAvailable(String driverId, String driverName, String vehicleBrand, String vehicleModel, int color, double rate, Place actualLocation) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.color = color;
        this.rate = rate;
        this.actualLocation = actualLocation;
    }

    public DriverAvailable() {
    }

    public static DriverAvailableBuilder builder(String id, String name, String vehicleBrand, String vehicleModel, int color, double rate) {
        final DriverAvailableBuilder driverAvailableBuilder = new DriverAvailableBuilder(id, name, vehicleBrand, vehicleModel, color, rate);
        return driverAvailableBuilder;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public int getColor() {
        return color;
    }

    public double getRate() {
        return rate;
    }

    public Place getActualLocation() {
        return actualLocation;
    }

    @Override
    public String toString() {
        return "DriverAvailable{" +
                "driverId='" + driverId + '\'' +
                ", driverName='" + driverName + '\'' +
                ", vehicleBrand='" + vehicleBrand + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", color=" + color +
                ", rate=" + rate +
                ", actualLocation=" + actualLocation +
                '}';
    }

    public Place getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Place startingPoint) {
        this.startingPoint = startingPoint;
    }

    public Place getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Place endPoint) {
        this.endPoint = endPoint;
    }

    public boolean isShuttedDown() {
        return shuttedDown;
    }

    public void setShuttedDown(boolean shuttedDown) {
        this.shuttedDown = shuttedDown;
    }

    @Override
    public String name() {
        return getDriverName();
    }

    @Override
    public Place startPoint() {
        return getStartingPoint();
    }

    @Override
    public Place endPoint() {
        return getEndPoint();
    }

    public Fuel getFuel() {
        return fuel;
    }

    public void setFuel(Fuel fuel) {
        this.fuel = fuel;
    }

    public static class DriverAvailableBuilder {
        private String driverId, driverName, vehicleBrand, vehicleModel;
        private int color;
        private double rate;
        private Place actualLocation;
        private Place startingPoint;
        private Place endPoint;
        private boolean shuttedDown = false;
        private Fuel fuel;

        public DriverAvailableBuilder(String driverId, String driverName, String vehicleBrand, String vehicleModel, int color, double rate) {
            this.driverId = driverId;
            this.driverName = driverName;
            this.vehicleBrand = vehicleBrand;
            this.vehicleModel = vehicleModel;
            this.color = color;
            this.rate = rate;

        }

        public DriverAvailableBuilder actualLocation(Place actualLocation) {
            this.actualLocation = actualLocation;
            return this;
        }

        public DriverAvailableBuilder startingPoint(Place startingPoint) {
            this.startingPoint = startingPoint;
            return this;
        }

        public DriverAvailableBuilder endPoint(Place endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        public DriverAvailableBuilder shuttedDown(boolean shuttedDown) {
            this.shuttedDown = shuttedDown;
            return this;
        }

        public DriverAvailableBuilder fuel(Fuel fuel) {
            this.fuel = fuel;
            return this;
        }

        public DriverAvailable build() {
            return new DriverAvailable(this);
        }


    }

    @NonNull
    public String print() {
        return name() + "" + startPoint() + " " + endPoint();
    }
}
