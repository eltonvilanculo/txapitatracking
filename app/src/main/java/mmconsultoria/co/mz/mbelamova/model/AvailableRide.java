package mmconsultoria.co.mz.mbelamova.model;

public class AvailableRide {
    private Ride ride;
    private Place startPoint;
    private Place endPoint;
    private double distance;
    private double price;

    public AvailableRide() {
    }

    public AvailableRide(Ride ride, Place startPoint, Place endPoint, double distance, double price) {
        this.ride = ride;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distance = distance;
        this.price = price;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "AvailableRide{" +
                "ride=" + ride +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", distance=" + distance +
                ", price=" + price +
                '}';
    }
}
