package mmconsultoria.co.mz.mbelamova.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.lifecycle.ViewModelProviders;
import io.reactivex.android.schedulers.AndroidSchedulers;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.view_model.AuthModel;
import mmconsultoria.co.mz.mbelamova.view_model.AuthService;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    //Splash_Screen
    private final static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //Splash Screen
        new Handler().postDelayed(() -> {
            ViewModelProviders.of(this).get(AuthModel.class).isUserSignedIn()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(authResult -> {
                        Timber.i("Result of authentication process: %s",authResult);
                                if (authResult == AuthService.AuthResult.SIGNED_IN) {
                                    startMyActivity(DriverMapsActivity.class);
                                    return;
                                }

                                startMyActivity(SignInActivity.class);
                            },
                            Timber::d);

        }, SPLASH_TIME_OUT);
    }
}



