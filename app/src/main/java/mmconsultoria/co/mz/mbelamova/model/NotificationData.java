package mmconsultoria.co.mz.mbelamova.model;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

public class NotificationData {

    public void init(){
        mContentTitle = "Bob's Post";
        mContentText = "[Picture] Like my shot of Earth?";
        mPriority = NotificationCompat.PRIORITY_HIGH;

        // Notification channel values (for devices targeting 26 and above):
        mChannelId = "channel_social_1";
        // The user-visible name of the channel.
        mChannelName = "Sample Social";
        // The user-visible description of the channel.
        mChannelDescription = "Sample Social Notifications";
        mChannelImportance = NotificationManager.IMPORTANCE_HIGH;
        mChannelEnableVibrate = true;
        mChannelLockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;
    }

    protected String mContentTitle;
    protected String mContentText;
    protected int mPriority;

    // Notification channel values (O and above):
    protected String mChannelId;
    protected CharSequence mChannelName;
    protected String mChannelDescription;
    protected int mChannelImportance;
    protected boolean mChannelEnableVibrate;
    protected int mChannelLockscreenVisibility;

    // Notification Standard notification get methods:
    public String getContentTitle() {
        return mContentTitle;
    }

    public String getContentText() {
        return mContentText;
    }

    public int getPriority() {
        return mPriority;
    }

    // Channel values (O and above) get methods:
    public String getChannelId() {
        return mChannelId;
    }

    public CharSequence getChannelName() {
        return mChannelName;
    }

    public String getChannelDescription() {
        return mChannelDescription;
    }

    public int getChannelImportance() {
        return mChannelImportance;
    }

    public boolean isChannelEnableVibrate() {
        return mChannelEnableVibrate;
    }

    public int getChannelLockscreenVisibility() {
        return mChannelLockscreenVisibility;
    }


    public static Uri resourceToUri(Context context, int resId) {
        return Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://"
                        + context.getResources().getResourcePackageName(resId)
                        + "/"
                        + context.getResources().getResourceTypeName(resId)
                        + "/"
                        + context.getResources().getResourceEntryName(resId));
    }
}
