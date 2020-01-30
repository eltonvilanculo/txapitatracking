package mmconsultoria.co.mz.mbelamova.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Vector;

import timber.log.Timber;

public class Controller {

    private String startName;
    volatile private double distance;
    private int numberOfAvailableSeats;
    private String currentFocus = "";
    public static final String FOCUS_START = "START";
    public static final String FOCUS_END = "END";

    private Controller() {
    }

    private static Controller instance;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }

        return instance;
    }

    private String destinationName;
    private LatLng currentLocation;
    private LatLng start = null;
    private LatLng destination;
    private Person person;

    public int getPLACE_PICKER_REQUEST() {
        return PLACE_PICKER_REQUEST;
    }

    private final int PLACE_PICKER_REQUEST = 111;
    private Vector OnDrivers;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destinationLatLng) {
        this.destination = destinationLatLng;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destination) {
        this.destinationName = destination;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LatLng getStart() {
        return start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        Timber.d("thread: %s,distance: %s",Thread.currentThread().getName(), distance);
        this.distance = distance;
    }

    public void setNumberOfAvailableSeats(int numberOfAvailableSeats) {
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }

    public int getNumberOfAvailableSeats() {
        return numberOfAvailableSeats;
    }

    public void setCurrentFocus(String currentFocus) {
        this.currentFocus = currentFocus;
    }

    public String getCurrentFocus() {
        return currentFocus;
    }
}
