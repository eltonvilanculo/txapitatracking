package mmconsultoria.co.mz.mbelamova.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.fragment.ProgressDialog;
import mmconsultoria.co.mz.mbelamova.fragment.VehicleSignUp;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.view_model.AuthModel;
import mmconsultoria.co.mz.mbelamova.view_model.AuthService;
import timber.log.Timber;

public class SignUpEmailFragment extends BaseActivity implements Validator.ValidationListener {


    @NotEmpty
    @Email
    public EditText emailTxt;
    @NotEmpty
    public EditText nameTxt;
    @NotEmpty
    public EditText lastNameTxt;

    public EditText phoneTxt;
    public EditText birthTxt;

    @Password(min = 6, scheme = Password.Scheme.ANY)
    public EditText passwordTxt;
    @ConfirmPassword
    public EditText confirmPassTxt;
    @Checked(message = "Aceite os termos e condicoes")
    public CheckBox isTermsAndCondsSelected;


    private CheckBox isCreateDriverSelected;
    private Validator validator;
    private AuthModel model;
    private ProgressDialog dialog;
    private int requestCode = 444;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.sign_up_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);

        setSupportActionBar(toolbar);

        nameTxt = findViewById(R.id.sign_up_first_name);
        lastNameTxt = findViewById(R.id.sign_up_last_name);
        emailTxt = findViewById(R.id.sign_up_email);
        phoneTxt = findViewById(R.id.sign_up_phone);
        passwordTxt = findViewById(R.id.sign_up_pass);
        birthTxt=findViewById(R.id.sign_up_date_of_birth);
        confirmPassTxt = findViewById(R.id.sign_up_confirm_pass);

        isTermsAndCondsSelected = findViewById(R.id.checkbox);
        isCreateDriverSelected = findViewById(R.id.checkboxTipoUser);

        validator = new Validator(this);
        validator.setValidationListener(this);

        model = ViewModelProviders.of(this).get(AuthModel.class);
        model.getSignInStatus().observe(this, this::onSignUp);

    }

    private void onSignUp(AuthService.AuthResult result) {
        switch (result) {
            case PROCCESSIGN:
                dialog = new ProgressDialog(this);
                dialog.setText("Aguarde... " );
                dialog.show();
                break;

            case USER_CREATED:
                if (!isCreateDriverSelected.isChecked()) {
                    startMyActivity(DriverMapsActivity.class);
                    Toast.makeText(this, "Passou Cadastro!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSignUp: "+"Passou Cadastro");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(DatabaseValue.USER_ID.name(), result.getKey());
                    startActivity(VehicleSignUp.class, bundle, null);
                }
                break;
            default:
                Toast.makeText(this, "Erro ao criar conta", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                break;

        }
    }


    private Person createPerson(String firstName, String lastName, long dateOfBirth, String email, String phoneNumber) {
        Person person = new Person();
        person.setName(firstName);
        person.setLastName(lastName);
        person.setCirculationArea(dateOfBirth);
        person.setEmail(email);
        person.setPhoneNumber(phoneNumber);

        return person;
    }

    @Override
    public void onValidationSucceeded() {
        String firstName = nameTxt.getText().toString();
        String lastName = lastNameTxt.getText().toString();
        long circulationArea = 50;
        String email = emailTxt.getText().toString();
        String phoneNumber = phoneTxt.getText().toString();

        Person person = createPerson(firstName, lastName, circulationArea, email, phoneNumber);

        Timber.i("person: %s ",person);

        model.signUp(person, getText(passwordTxt));
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

    @OnClick(R.id.sign_up_email_sign_up_item)
    public void signUp(View view) {
        validator.validate();
    }


    public void signIn(View view) {
        validator.validate();
//        startMyActivity(SignInActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == this.requestCode && resultCode == RESULT_OK && data != null) {
//            imageUri = data.getData();
//
//            try {
//                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                userImage.setImageBitmap(imageBitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//
//    }


}