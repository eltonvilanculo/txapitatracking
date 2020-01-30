package mmconsultoria.co.mz.mbelamova.service;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import timber.log.Timber;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.d("onMessageReceived");
        LatLng custumer_location=new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);

        Intent intent=new Intent(getBaseContext(),CustommerCall.class);
        intent.putExtra("lat",custumer_location.latitude);
        intent.putExtra("long",custumer_location.longitude);
        startActivity(intent);

    }
}
