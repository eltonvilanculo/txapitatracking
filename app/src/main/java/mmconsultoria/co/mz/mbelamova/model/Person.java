package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

import mmconsultoria.co.mz.mbelamova.cloud.ImageHolder;

@IgnoreExtraProperties
public class Person implements Parcelable, ImageHolder {
    @Exclude
    private String id,notificationId;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String photoUri;
    private List<Place> userPlaces;
    private String lastName;
    private long dateOfSignUp;
    private long circulationArea;
    private transient double moneyAvailable;
    private boolean isEnabled = false;
    private boolean isMaleGender;
    private DriverData driverData;
    private PersonReview review;
    private StatsResume stats;


    public Person() {
    }

    public Person(String firstName, String lastName, String id, boolean isMaleGender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.isMaleGender = isMaleGender;
    }

    public Person(String firstName, String lastName,  boolean isMaleGender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isMaleGender = isMaleGender;
    }

    protected Person(Parcel in) {
        firstName = in.readString();
        id = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        photoUri = in.readString();
        userPlaces = in.createTypedArrayList(Place.CREATOR);
        lastName = in.readString();
        dateOfSignUp = in.readLong();
        circulationArea = in.readLong();
        moneyAvailable = in.readDouble();
        isEnabled = in.readByte() != 0;
        isMaleGender = in.readByte() != 0;
        driverData = in.readParcelable(DriverData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(photoUri);
        dest.writeTypedList(userPlaces);
        dest.writeString(lastName);
        dest.writeLong(dateOfSignUp);
        dest.writeLong(circulationArea);
        dest.writeDouble(moneyAvailable);
        dest.writeByte((byte) (isEnabled ? 1 : 0));
        dest.writeByte((byte) (isMaleGender ? 1 : 0));
        dest.writeParcelable(driverData, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getName() {
        return firstName;
    }

    public void setName(String name) {
        this.firstName = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getDateOfSignUp() {
        return dateOfSignUp;
    }

    public void setDateOfSignUp(long dateOfSignUp) {
        this.dateOfSignUp = dateOfSignUp;
    }

    public long getCirculationArea() {
        return circulationArea;
    }

    public void setCirculationArea(long circulationArea) {
        this.circulationArea = circulationArea;
    }

    public String retrieveFullName() {
        return firstName + " " + lastName;
    }

    public double getMoneyAvailable() {
        return moneyAvailable;
    }

    public void setMoneyAvailable(double moneyAvailable) {
        this.moneyAvailable = moneyAvailable;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }


    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", notificationId='" + notificationId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", userPlaces=" + userPlaces +
                ", lastName='" + lastName + '\'' +
                ", dateOfSignUp=" + dateOfSignUp +
                ", circulationArea=" + circulationArea +
                ", moneyAvailable=" + moneyAvailable +
                ", isEnabled=" + isEnabled +
                ", isMaleGender=" + isMaleGender +
                ", driverData=" + driverData +
                ", review=" + review +
                ", stats=" + stats +
                '}';
    }

    public boolean isMaleGender() {
        return isMaleGender;
    }

    public void setMaleGender(boolean maleGender) {
        isMaleGender = maleGender;
    }

    public DriverData getDriverData() {
        return driverData;
    }

    public void setDriverData(DriverData driverData) {
        this.driverData = driverData;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public PersonReview getReview() {
        return review;
    }

    public void setReview(PersonReview review) {
        this.review = review;
    }

    public StatsResume getStats() {
        return stats;
    }

    public void setStats(StatsResume stats) {
        this.stats = stats;
    }
}
