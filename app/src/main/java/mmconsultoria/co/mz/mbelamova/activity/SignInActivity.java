package mmconsultoria.co.mz.mbelamova.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.fragment.ProgressDialog;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.view_model.AuthModel;
import mmconsultoria.co.mz.mbelamova.view_model.AuthService;
import timber.log.Timber;


public class SignInActivity extends BaseActivity implements Validator.ValidationListener {

    private Toolbar toolbar;
    @NotEmpty
    @Email
    private EditText email;
    @Password
    private EditText password;
    private CheckBox checkBox;


    private Validator validator;
    private AuthModel model;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_by_gam);


        toolbar = findViewById(R.id.sign_in_toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);

        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_pass);

        checkBox = findViewById(R.id.checkbox);

        validator = new Validator(this);
        validator.setValidationListener(this);

        model = ViewModelProviders.of(this).get(AuthModel.class);
        model.getSignInStatus().observe(this, this::onSignIn);
    }

    private void onSignIn(AuthService.AuthResult result) {

        switch (result) {
            case PROCCESSIGN:
                dialog = new ProgressDialog(this);
                dialog.setText("Please Wait...");
                dialog.show();
                break;

            case SIGNED_IN:
                Timber.d("SignInId: %s", result.getKey());

                subscribeToNotification(result.getKey());

                disposeDialog();
                startMyActivity(ClientMapActivity.class);
                break;
            case ERROR:
                Toast.makeText(this, "Crie uma Conta", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                disposeDialog();
                break;

        }

    }


    private void disposeDialog() {
        if (dialog == null) {
            return;
        }

        dialog.dismiss();
    }


    public void signIn(View view) {
        validator.validate();
    }


    @Override
    public void onValidationSucceeded() {
        String email = getText(this.email);
        String password = getText(this.password);

        model.signInUser(email, password);


    }


    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void signUp(View view) {
        startMyActivity(SignUpEmailFragment.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dialog != null)
            dialog.dismiss();
    }


}