package mmconsultoria.co.mz.mbelamova;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import mmconsultoria.co.mz.mbelamova.model.ReleaseTree;
import timber.log.Timber;

public class MbelaApp extends MultiDexApplication {
    public static final String MBELA_PAYMENT = "MBELA_PAYMENT";
    public static final String MBELA_TRIP_DATA = "MBELA_TRIP_DATA";
    public static final String MBELA_NOTIFY = "MBELA_NOTIFY";

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected @Nullable String createStackElementTag(@NotNull StackTraceElement element) {
                    return super.createStackElementTag(element) + ": line ==> " + element.getLineNumber() + "/[" + element.getMethodName() + "]";
                }
            });
        } else Timber.plant(new ReleaseTree());

createNotificationChannel();


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(MbelaApp.this);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel highChannel = new NotificationChannel(MBELA_PAYMENT, MBELA_NOTIFY, NotificationManager.IMPORTANCE_HIGH);
            highChannel.setDescription("My High Description");
            NotificationChannel lowChannel = new NotificationChannel(MBELA_TRIP_DATA, MBELA_NOTIFY, NotificationManager.IMPORTANCE_HIGH);
            lowChannel.setDescription("My Low Description");


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(highChannel);
            manager.createNotificationChannel(lowChannel);
        }
    }
}
