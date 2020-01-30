package mmconsultoria.co.mz.mbelamova.service;

import android.app.Application;
import android.text.TextUtils;

import mmconsultoria.co.mz.mbelamova.cloud.CloudRepository;
import mmconsultoria.co.mz.mbelamova.model.Person;
import timber.log.Timber;

public class ServerUpdater {
    private static ServerUpdater instance;
    private String notificationId;
    private String userId;

    private ServerUpdater() {
    }

    public static ServerUpdater start() {
        if (instance == null) {
            instance = new ServerUpdater();
        }

        return instance;
    }

    public ServerUpdater notifyId(String notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public ServerUpdater userId(String userId) {
        this.userId = userId;
        return this;
    }

    public void sendWith(Application application) {
        if (TextUtils.isEmpty(userId)) {
            Timber.i("userId is empty");
            return;
        }
        if (TextUtils.isEmpty(notificationId)) {
            Timber.i("NotificationId is empty");
            return;
        }

        updateNotificationId(application);
    }

    public void destroy() {
        instance = null;
    }

    private void updateNotificationId(Application application) {
        Timber.i("Attempting to update the notificationId");
        new CloudRepository<>(application, Person.class)
                .setPath(userId)
                .updateField("notificationId", notificationId)
                .doFinally(() -> destroy())
                .subscribe(success -> Timber.i("notificationId updated"), throwable -> {
                    Timber.d(throwable, "notificationId update failed!");
                });
    }


}
