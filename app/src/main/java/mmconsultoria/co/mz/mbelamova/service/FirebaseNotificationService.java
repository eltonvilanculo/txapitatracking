package mmconsultoria.co.mz.mbelamova.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import mmconsultoria.co.mz.mbelamova.MbelaApp;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.activity.ClientMapActivity;
import mmconsultoria.co.mz.mbelamova.activity.DriverMapsActivity;
import mmconsultoria.co.mz.mbelamova.activity.NotificationActivity;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.model.Ride;
import mmconsultoria.co.mz.mbelamova.model.TripStatus;
import mmconsultoria.co.mz.mbelamova.util.LocalDataUtils;
import timber.log.Timber;

public class FirebaseNotificationService extends FirebaseMessagingService {
    public static final String CLIENT_NOTIFICATION = "CLIENT_NOTIFICATION";
    public static final String TRIP_NOTIFICATION_ACTION = "TRIP_NOTIFICATION_ACTION";
    public static final String NOTIFICATION_BUNDLE = "EXTRA_NOTIFICATION";
    public static final String PORPOSE_PAYMENT = "PAYMENT";
    public static final String PORPOSE_TRIP_SCHEDULE = "TRIP_SCHEDULE";
    public static final String PORPOSE_STATUS_MESSAGE = "TRIP_STATUS_MESSAGE";


    public static final String ACTION_ACCEPT = "mmconsultoria.co.mz.mbelamova" + ".handler.action_ACCEPT";
    public static final String EXTRA_ACCEPT = "mmconsultoria.co.mz.mbelamova" + ".handler.extra.ACCEPT";
    public static final String ACTION_REJECT = "mmconsultoria.co.mz.mbelamova" + ".handler.action_REJECT";
    public static final String EXTRA_REJECT = "mmconsultoria.co.mz.mbelamova" + ".handler.extra_REJECT";
    public static final String TRIP_NOTIFICATION_ACTION_CLIENT = "mmconsultoria.co.mz.mbelamova.action.NOTIFICATION_CLIENT";
    public static final String TRIP_NOTIFICATION_ACTION_DRIVER = "mmconsultoria.co.mz.mbelamova.action.NOTIFICATION_DRIVER";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        Timber.d("Received Message -> Sender: %s, data: %s", data.get("sender"), data);

        if (data.isEmpty()) {
            return;
        }


        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Timber.i("key: %s, data: %s", entry.getKey(), entry.getValue());
            bundle.putString(entry.getKey(), entry.getValue());
        }


        Timber.d("threadName: %s", Thread.currentThread().getName());
        Class aClass = NotificationActivity.class;
        String status = bundle.getString("status");
        String purpose = bundle.getString("purpose");

        if (TextUtils.isEmpty(purpose)) {

        } else {
            if (purpose.equals(PORPOSE_STATUS_MESSAGE)) {
                if (status.contains("CLIENT")) {
                    aClass = DriverMapsActivity.class;
                } else {
                    aClass = ClientMapActivity.class;
                }
            } else {
                aClass = ClientMapActivity.class;
            }
        }

        Intent intent;


//
//
//        if (DriverMapsActivity.getInstance() != null) {
//
//            Timber.d("driverState: %s", DriverMapsActivity.getInstance().getCurrentState());
//            intent = new Intent();
//            intent.setAction(TRIP_NOTIFICATION_ACTION_DRIVER);
//            intent.putExtra(NOTIFICATION_BUNDLE, bundle);
//            LocalBroadcastManager.getInstance(FirebaseNotificationService.this).sendBroadcast(intent);
//        }
//
//        if (ClientMapActivity.getInstance() != null) {
//            Timber.d("clientState: %s", ClientMapActivity.getInstance().getCurrentState());
//
//            intent = new Intent();
//            intent.setAction(TRIP_NOTIFICATION_ACTION_CLIENT);
//            intent.putExtra(NOTIFICATION_BUNDLE, bundle);
//
//            Timber.d("Bundle Data -> Sender: %s", bundle.get("sender"));
//            LocalBroadcastManager.getInstance(FirebaseNotificationService.this).sendBroadcast(intent);
//
//        } else {


        intent = new Intent(this, aClass);
        intent.setAction(TRIP_NOTIFICATION_ACTION);
        intent.putExtra(NOTIFICATION_BUNDLE, bundle);
        NotificationManagerCompat.from(FirebaseNotificationService.this).notify(1, createNotification(bundle, intent, aClass));
        // }


    }


    public Notification createNotification(Bundle bundle, Intent mainIntent, Class aclass) {
        String notificationChannelId = MbelaApp.MBELA_TRIP_DATA;


        String subTitle = bundle.getString("senderName");

        String purpose = bundle.getString("purpose");
        String senderName = bundle.getString("senderName");
        String messageType = bundle.getString("messageType");
        String rideId = bundle.getString("rideId");
        String tripId = bundle.getString("tripId");
        String status = bundle.getString("status");
        String driverId = bundle.getString("driverId");
        String clientId = bundle.getString("clientId");
        String clientName = bundle.getString("clientName");
        String driverName = bundle.getString("driverName");
        String rides = bundle.getString("rides");

        String title = "Pedido de viagem";

        if (purpose.equals(PORPOSE_TRIP_SCHEDULE)) {
            title = "Agendamento de viagem";
        }

        if (purpose.equals(PORPOSE_PAYMENT)) {
            title = "Pagamento";
            String code = bundle.getString("code");
            notificationChannelId = MbelaApp.MBELA_PAYMENT;

        }

        if (purpose.equals(PORPOSE_STATUS_MESSAGE)) {
            title = "Actualizacao do estado de viagem";


            try {
                TripStatus tripStatus = TripStatus.valueOf(bundle.getString("status"));

                switch (tripStatus) {
                    case IN_PROGRESS:
                        title = "Viagem comecou";

                        saveTripLocally(rideId, tripId);
                        break;
                    case CLIENT_CANCELED:
                        title = "Viagem Cancelada";
                        break;
                    case SCHEDULED:
                        saveRideLocally(rideId);
                        title = "Viagem agendada";
                        break;
                    case DRIVER_REJECTED:
                        title = "Pedido rejeitado";
                        break;
                    case DRIVER_CANCELED:
                        title = "Viagem Cancelada";
                        break;
                    case CLIENT_REQUESTED:
                        title = "Pedido de viagem";
                        break;
                    case DRIVER_CONFIRMED:
                        title = "Viagem aceite";
                        break;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }


        String image = bundle.getString("image");



        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).


        // 2. Build the BIG_PICTURE_STYLE.
        NotificationCompat.BigPictureStyle bigPictureStyle = null
                // Summary line after the detail section in the big form of the template.
                ;



        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack.
        stackBuilder.addParentStack(aclass);
        // Adds the Intent to the top of the stack.
        stackBuilder.addNextIntent(mainIntent);
        // Gets a PendingIntent containing the entire back stack.
        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );






        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for a comment on the post.

        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);


        notificationCompatBuilder
                // BIG_PICTURE_STYLE sets title and content for API 16 (4.1 and after).

                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(title)
                // Content for API <24 (7.0 and below) devices.
                .setContentText(subTitle)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_person_black_48dp))
                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)


                .setCategory(Notification.CATEGORY_SOCIAL);

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.


                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen;

//        // If the phone is in "Do not disturb mode, the user will still be notified if
//        // the sender(s) is starred as a favorite.
//        for (String name : bigPictureStyleSocialAppData.getParticipants()) {
//            notificationCompatBuilder.addPerson(name);
//        }

        Notification notification = notificationCompatBuilder.build();
        notification.flags |= NotificationCompat.FLAG_AUTO_CANCEL;

//        mNotificationManagerCompat.notify(MbelaApp.MBELA_NOTIFY, notification);
//        Notification notification = new NotificationCompat.Builder(this, MbelaApp.MBELA_PAYMENT)
//                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(bundle.getString("senderName", "title"))
//                .setContentText(bundle.getString("message", "no messege"))
//                .setVibrate(new long[]{
//                        1000L, 500L, 2000L
//                })
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .build();


        return notification;


    }

    private void saveTripLocally(String rideId, String tripId) {
        Observable.just(rideId)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Ride")
                            .child(rideId)
                            .child("trips")
                            .child(tripId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Ride ride = dataSnapshot.getValue(Ride.class);
                            if (ride != null) {
                                LocalDataUtils.from(getApplication()).saveObject(DatabaseValue.LAST_RIDE.name(), ride);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                });
    }

    private void saveRideLocally(String rideId) {
        Observable.just(rideId)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Ride")
                            .child(rideId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Ride ride = dataSnapshot.getValue(Ride.class);
                            if (ride != null) {
                                LocalDataUtils.from(getApplication()).saveObject(DatabaseValue.LAST_RIDE.name(), ride);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                });
    }

    @Override
    public void onNewToken(String token) {
        Timber.e("Refreshed token: %s", token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }


}
