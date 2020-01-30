package mmconsultoria.co.mz.mbelamova.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.activity.ClientMapActivity;
import mmconsultoria.co.mz.mbelamova.model.BaseFragment;
import mmconsultoria.co.mz.mbelamova.model.ScreenSwitcher;
import mmconsultoria.co.mz.mbelamova.model.SimpleCallback;
import mmconsultoria.co.mz.mbelamova.view_model.AuthModel;
import mmconsultoria.co.mz.mbelamova.view_model.AuthService;
import timber.log.Timber;

import static java.lang.String.valueOf;
import static mmconsultoria.co.mz.mbelamova.R2.id.ic_gps;
import static mmconsultoria.co.mz.mbelamova.R2.id.login_container;
import static mmconsultoria.co.mz.mbelamova.view_model.AuthService.AuthResult.SIGN_IN_SUCCESSFUL;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements SimpleCallback<String> {
    @BindView(R.id.login_fragment_phone_number_text)
    public EditText phoneNumber;
    @BindView(R.id.login_fragment_btn)
    public ImageButton loginBtn;

    private final String MOZ_AREA_CODE = "+258";
    private ProgressDialog dialog;
    private AuthModel auth;
    private ScreenSwitcher switcher;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Timber.i("+==> creating view");
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("+==> activity created");
        auth = ViewModelProviders.of(getActivity())
                .get(AuthModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("+==> starting");
        loginBtn.setOnClickListener(this::login);
    }

    public void login(View view) {
        if (validatePhoneNumberText()) {
            login(MOZ_AREA_CODE + phoneNumber.getText().toString().trim());
        }
    }

    private void login(String number) {
        dialog = new ProgressDialog(getActivity());
        dialog.show();


        auth.searchUserByPhoneNumber(number)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next -> {
                    Timber.d(valueOf(next));
                    if (next == AuthService.AuthResult.USER_DOES_NOT_EXISTS) {
                        auth.setVerificationType(AuthService.AuthResult.SIGN_UP);
                        signUpUser(number, auth);
                    } else if (next == AuthService.AuthResult.USER_EXISTS) {
                        auth.setVerificationType(AuthService.AuthResult.SIGN_IN);
                        auth.signIn(number, getActivity())
                                .doOnComplete(() -> {
                                    Timber.d("complete auth");
                                    startActivity(ClientMapActivity.class);
                                })
                                .subscribe(authResult -> signInSuccess(authResult, number, false), this::onSignInError);

                    }

                }, this::onSignInError);

    }


    private void signUpUser(String number, AuthModel auth) {
        Timber.d(number);
        auth.signIn(number, getActivity())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnError(this::onSignInError)
                .doOnNext(authResult ->
                        signInSuccess(authResult, number, true))
                .doOnComplete(() -> signInSuccess(SIGN_IN_SUCCESSFUL, number, true))
                .subscribe();


    }

    private void signInSuccess(AuthService.AuthResult authResult, String number, boolean isNewUser) {
        dialog.dismiss();
        Timber.d("AuthResult = %s IsNewUser %s ", authResult, isNewUser);

        if (authResult == AuthService.AuthResult.ERR_NETWORK) {
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (authResult == SIGN_IN_SUCCESSFUL) {
            if (isNewUser) {

                if (switcher != null) {
                    //switcher.switchToFragment(login_container, SignUpFragment.newInstance(number), null);
                }
            } else {

                if (switcher != null) {
                    switcher.switchToActivity(ClientMapActivity.class, null, null);
                }
            }
            return;
        }
        if (authResult == AuthService.AuthResult.CODE_SENT) {
            if (switcher != null)
                switcher.switchToFragment(login_container, VerifySMSCodeFragment.newInstance(number), null);


    }

}


    private void onSignInError(Throwable throwable) {
        Timber.e(throwable);
        dialog.dismiss();
    }


    private boolean validatePhoneNumberText() {
        if (phoneNumber.getText().toString().trim().isEmpty()) {
            phoneNumber.setError(getActivity().getString(R.string.write_number));
            return false;
        }

        return true;
    }

    @Override
    public void onSuccess(String data) {
        String trim = data.trim();
        if (trim.startsWith(MOZ_AREA_CODE)) {
            trim = trim.substring(4, 13);
        }
        phoneNumber.setText(trim);
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("+==> stopping");
        dialog.dismiss();
        switcher = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("+==> destroying");
    }

    public void setScreenSwitcher(ScreenSwitcher switcher) {
        this.switcher = switcher;
    }


}
