package mmconsultoria.co.mz.mbelamova.model;

import com.google.gson.annotations.SerializedName;

public class SubData {
    public static final String REQUEST_RIDE_TYPE = "REQUEST_RIDE";
    public static final String REJECT_RIDE_TYPE = "REJECT_RIDE";
    public static final String ACCEPT_RIDE_TYPE = "ACCEPT_RIDE";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String CANCEL_RIDE_TYPE = "CANCEL_RIDE";
    public static final String CONFIRM_RIDE_TYPE = "CONFIRM_RIDE";

    @SerializedName("pourpose")
    private String porpouse;
    @SerializedName("message")
    private String message;

    public static final String CLIENT_ROLE = "CLIENT";
    public static final String ANY_ROLE = "ANY";
    public static final String DRIVER_ROLE = "DRIVER";

    private double distance;

    private String receiverRole;
    private String sender;
    private String senderName;
    private double startLatitude;
    private double startLongitude;
    private double endLongitude;
    private double endLatitude;
    private String tripId;
    private String senderId;
    private String messageType;
    private String rideId;

    public String getPorpouse() {
        return porpouse;
    }

    public void setPorpouse(String porpouse) {
        this.porpouse = porpouse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "porpouse:'" + porpouse + '\'' +
                ", message:'" + message + '\'' +
                '}';
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getRideId() {
        return rideId;
    }
}
