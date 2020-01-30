package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StatsResume implements Parcelable {
    private int clientCanceledTrips;
    private int driverCanceledTrips;
    private int finishedTrips;
    private int scheduledTrips;
    private int clientGiveUpTrips;
    private int driverGiveUpTrips;
    private int canceledAwaitingPaymentTrip;

    public StatsResume() {
    }

    public StatsResume(int clientCanceledTrips, int driverCanceledTrips, int finishedTrips, int scheduledTrips, int clientGiveUpTrips, int driverGiveUpTrips, int canceledAwaitingPaymentTrip) {
        this.clientCanceledTrips = clientCanceledTrips;
        this.driverCanceledTrips = driverCanceledTrips;
        this.finishedTrips = finishedTrips;
        this.scheduledTrips = scheduledTrips;
        this.clientGiveUpTrips = clientGiveUpTrips;
        this.driverGiveUpTrips = driverGiveUpTrips;
        this.canceledAwaitingPaymentTrip = canceledAwaitingPaymentTrip;
    }

    protected StatsResume(Parcel in) {
        clientCanceledTrips = in.readInt();
        driverCanceledTrips = in.readInt();
        finishedTrips = in.readInt();
        scheduledTrips = in.readInt();
        clientGiveUpTrips = in.readInt();
        driverGiveUpTrips = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(clientCanceledTrips);
        dest.writeInt(driverCanceledTrips);
        dest.writeInt(finishedTrips);
        dest.writeInt(scheduledTrips);
        dest.writeInt(clientGiveUpTrips);
        dest.writeInt(driverGiveUpTrips);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StatsResume> CREATOR = new Creator<StatsResume>() {
        @Override
        public StatsResume createFromParcel(Parcel in) {
            return new StatsResume(in);
        }

        @Override
        public StatsResume[] newArray(int size) {
            return new StatsResume[size];
        }
    };

    public int getClientCanceledTrips() {
        return clientCanceledTrips;
    }

    public void setClientCanceledTrips(int clientCanceledTrips) {
        this.clientCanceledTrips = clientCanceledTrips;
    }

    public int getDriverCanceledTrips() {
        return driverCanceledTrips;
    }

    public void setDriverCanceledTrips(int driverCanceledTrips) {
        this.driverCanceledTrips = driverCanceledTrips;
    }

    public int getFinishedTrips() {
        return finishedTrips;
    }

    public void setFinishedTrips(int finishedTrips) {
        this.finishedTrips = finishedTrips;
    }

    public int getScheduledTrips() {
        return scheduledTrips;
    }

    public void setScheduledTrips(int scheduledTrips) {
        this.scheduledTrips = scheduledTrips;
    }

    public int getClientGiveUpTrips() {
        return clientGiveUpTrips;
    }

    public void setClientGiveUpTrips(int clientGiveUpTrips) {
        this.clientGiveUpTrips = clientGiveUpTrips;
    }

    public int getDriverGiveUpTrips() {
        return driverGiveUpTrips;
    }

    public void setDriverGiveUpTrips(int driverGiveUpTrips) {
        this.driverGiveUpTrips = driverGiveUpTrips;
    }

    public int getCanceledAwaitingPaymentTrip() {
        return canceledAwaitingPaymentTrip;
    }

    public void setCanceledAwaitingPaymentTrip(int canceledAwaitingPaymentTrip) {
        this.canceledAwaitingPaymentTrip = canceledAwaitingPaymentTrip;
    }

    @Override
    public String toString() {
        return "StatsResume{" +
                "clientCanceledTrips=" + clientCanceledTrips +
                ", driverCanceledTrips=" + driverCanceledTrips +
                ", finishedTrips=" + finishedTrips +
                ", scheduledTrips=" + scheduledTrips +
                ", clientGiveUpTrips=" + clientGiveUpTrips +
                ", driverGiveUpTrips=" + driverGiveUpTrips +
                ", canceledAwaitingPaymentTrip=" + canceledAwaitingPaymentTrip +
                '}';
    }
}
