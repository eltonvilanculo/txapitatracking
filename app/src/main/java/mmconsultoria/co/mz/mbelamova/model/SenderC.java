package mmconsultoria.co.mz.mbelamova.model;

public class SenderC { 
    public String to;

    public NotificationC notification;

    public SenderC(String to, NotificationC notification) {
        this.to = to;
        this.notification = notification;
    }

    public SenderC(){

    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationC getNotification() {
        return notification;
    }

    public void setNotification(NotificationC notification) {
        this.notification = notification;
    }
}
