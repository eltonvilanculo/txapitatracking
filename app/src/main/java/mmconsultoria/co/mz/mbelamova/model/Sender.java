package mmconsultoria.co.mz.mbelamova.model;

import android.app.Notification;

public class Sender {
    public Sender to;

    public Notification notification;

    public Sender(Sender to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public Sender(Sender to) {

    }

    public Sender getTo() {
        return to;
    }

    public void setTo(Sender to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
