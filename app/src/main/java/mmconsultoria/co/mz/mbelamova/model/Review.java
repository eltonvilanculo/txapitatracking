package mmconsultoria.co.mz.mbelamova.model;

public class Review {
    private String comment;
    private String ridId;
    private int score;

    public Review() {
    }

    public Review(String comment, String ridId, int score) {
        this.comment = comment;
        this.ridId = ridId;
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRidId() {
        return ridId;
    }

    public void setRidId(String ridId) {
        this.ridId = ridId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Review{" +
                "comment='" + comment + '\'' +
                ", ridId='" + ridId + '\'' +
                ", score=" + score +
                '}';
    }
}
