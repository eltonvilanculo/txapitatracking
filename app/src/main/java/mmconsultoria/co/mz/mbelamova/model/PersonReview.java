package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PersonReview implements Parcelable {
    private List<Integer> driver;
    private List<Integer> client;

    protected PersonReview(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PersonReview> CREATOR = new Creator<PersonReview>() {
        @Override
        public PersonReview createFromParcel(Parcel in) {
            return new PersonReview(in);
        }

        @Override
        public PersonReview[] newArray(int size) {
            return new PersonReview[size];
        }
    };

    @Override
    public String toString() {
        return "PersonReview{" +
                "driver=" + driver +
                ", client=" + client +
                '}';
    }

    public PersonReview() {
    }

    public PersonReview(List<Integer> driver, List<Integer> client) {
        this.driver = driver;
        this.client = client;
    }

    public List<Integer> getDriver() {
        return driver;
    }

    public void setDriver(List<Integer> driver) {
        this.driver = driver;
    }

    public List<Integer> getClient() {
        return client;
    }

    public void setClient(List<Integer> client) {
        this.client = client;
    }

    public float clientResume() {
        return resume(client);
    }

    public float driverResume() {
        return resume(driver);
    }

    private float resume(List<Integer> map) {
        if (map == null || map.isEmpty()) {
            return 0;
        }

        int one = map.get(1) == null ? 0 : map.get(1);
        int two = map.get(2) == null ? 0 : map.get(2);
        int tree = map.get(3) == null ? 0 : map.get(3);
        int four = map.get(4) == null ? 0 : map.get(4);
        int five = map.get(5) == null ? 0 : map.get(5);



        float num = 5;

        if (one == 0) {
            num--;
        }


        if (two == 0) {
            num--;
        }


        if (tree == 0) {
            num--;
        }


        if (four == 0) {
            num--;
        }


        if (five == 0) {
            num--;
        }

        if (num == 0) {
            return 0;
        }

        float total = one + two + tree + four + five;
        return (one + 2*two + 3*tree + 4*four + 5*five) / total;

    }
}
