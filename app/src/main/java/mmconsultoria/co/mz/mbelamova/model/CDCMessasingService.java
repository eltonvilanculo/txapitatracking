package mmconsultoria.co.mz.mbelamova.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;

public class CDCMessasingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        mostrarNotificacao(notification);
    }

   public void mostrarNotificacao(RemoteMessage.Notification notification) {

        //nao tenho a certeza se importei o correcto
        String titulo = notification.getTitle();
        String mensagem = notification.getBody();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(titulo)
                .setContentText(mensagem)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, new Notification());



    }


}
