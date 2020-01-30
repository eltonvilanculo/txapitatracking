package mmconsultoria.co.mz.mbelamova.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class RatingReview implements Parcelable {
    private Map<String, Integer> score = new HashMap<>();

    public RatingReview() {
    }

    public RatingReview(Map<String, Integer> score) {
        this.score = score;
    }

    protected RatingReview(Parcel in) {
        Bundle bundle = in.readBundle();

        for (String key : bundle.keySet()) {
            score.put(key, bundle.getInt(key));
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        for (Map.Entry<String, Integer> entry :
                score.entrySet()) {
            bundle.putInt(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RatingReview> CREATOR = new Creator<RatingReview>() {
        @Override
        public RatingReview createFromParcel(Parcel in) {
            return new RatingReview(in);
        }

        @Override
        public RatingReview[] newArray(int size) {
            return new RatingReview[size];
        }
    };

    public Map<String, Integer> getScore() {
        return score;
    }

    public void setScore(Map<String, Integer> score) {


        this.score = score;
        if (this.score == null) {
            this.score = new HashMap<>();
        }

    }

    public float resume() {
        if (score == null || score.isEmpty()) {
            return 0;
        }

        int one = score.get("1") == null ? 0 : score.get("1");
        int two = score.get("2") == null ? 0 : score.get("2");
        int tree = score.get("3") == null ? 0 : score.get("3");
        int four = score.get("4") == null ? 0 : score.get("4");
        int five = score.get("5") == null ? 0 : score.get("5");

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

        return (one + two + tree + four + five) / num;

    }

    @Override
    public String toString() {
        return "RatingReview{" +
                "score=" + score +
                '}';
    }
}
