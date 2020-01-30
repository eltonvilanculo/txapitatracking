package mmconsultoria.co.mz.mbelamova.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import androidx.lifecycle.ViewModelProviders;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.fragment.LoginFragment;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.BaseFragment;
import mmconsultoria.co.mz.mbelamova.model.ScreenSwitcher;
import mmconsultoria.co.mz.mbelamova.model.SimpleCallback;
import mmconsultoria.co.mz.mbelamova.view_model.AuthModel;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements ScreenSwitcher {

    private static final int PHONE_NUMBER_RC = 50;
    private LoginFragment loginFragment;
    private SimpleCallback<String> phoneNumberCallback;
    private GoogleApiClient googleApiClient;
    private AuthModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Timber.i("+==> creating");

        if (savedInstanceState == null) {
            loginFragment = new LoginFragment();
            loginFragment.setScreenSwitcher(this);

            requestPhoneNumber();

            model = ViewModelProviders.of(this)
                    .get(AuthModel.class);

            swapFragment(R.id.login_container, loginFragment, null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.i("+==> starting");
    }

    public void requestPhoneNumber(SimpleCallback<String> callback) {
        phoneNumberCallback = callback;
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), PHONE_NUMBER_RC, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Timber.e(e, "Could not start hint picker Intent");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_NUMBER_RC) {
            if (resultCode == RESULT_OK) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (phoneNumberCallback != null) {
                    phoneNumberCallback.onSuccess(cred.getId());
                    Timber.d("onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                }
            }
            phoneNumberCallback = null;
        }
    }

    private void requestPhoneNumber() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        requestPhoneNumber(loginFragment);
    }


    @Override
    public   <activity extends BaseActivity> void  switchToActivity(Class<activity> target, Bundle bundle, Parcelable parcelable) {
        startActivity(target,bundle,parcelable);
    }

    @Override
    public void switchToFragment(int container, BaseFragment fragment, String tag) {
        swapFragment(container,fragment,tag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("+==> destroying");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("+==> stopping");
    }
}