package mmconsultoria.co.mz.mbelamova.model;

import android.os.Bundle;
import android.os.Parcelable;

public interface ScreenSwitcher {
    <activity extends BaseActivity> void switchToActivity(Class<activity> target, Bundle bundle, Parcelable parcelable);

    void switchToFragment(int login_container, BaseFragment fragment, String tag);
}
