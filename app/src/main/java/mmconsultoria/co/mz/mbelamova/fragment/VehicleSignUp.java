package mmconsultoria.co.mz.mbelamova.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.savvisingh.colorpickerdialog.ColorPickerDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.activity.ClientMapActivity;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.DriverLicence;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Vehicle;
import mmconsultoria.co.mz.mbelamova.view_model.AuthModel;
import mmconsultoria.co.mz.mbelamova.view_model.AuthService;
import timber.log.Timber;


public class VehicleSignUp extends BaseActivity implements Validator.ValidationListener {

    @NotEmpty
    @BindView(R.id.vehicle_sign_up_manufacturer)
    public EditText manufacturer;
    @BindView(R.id.vehicle_sign_up_model)
    @NotEmpty
    public EditText model;
    @BindView(R.id.vehicle_sign_up_color)

    public EditText color;
    @BindView(R.id.vehicle_sign_up_fuel_type_spinner)
    public Spinner fuelTypeSpinner;

    @NotEmpty
    @BindView(R.id.vehicle_sign_up_driver_cylinders)
    public EditText numberOfCylinders;

    @NotEmpty
    @BindView(R.id.vehicle_sign_up_licence_plate)
    public EditText licencePlate;
    @BindView(R.id.vehicle_sign_up_year)
    @NotEmpty
    public EditText year;
    @NotEmpty
    @BindView(R.id.vehicle_sign_up_available_seats)
    public EditText available_seats;

    @NotEmpty
    @BindView(R.id.vehicle_sign_up_driver_licence)
    public EditText driverLicence;

    @BindView(R.id.vehicle_sign_up_choose_back_button)
    public ImageButton backButton;

    @BindView(R.id.vehicle_sign_up_submit_btn)
    public Button submitBtn;


    private Validator validator;
    private AuthModel authModel;
    private Person person;
    private ProgressDialog dialog;
    private MutableLiveData<AuthService.AuthResult> updateUser;
    private int selectedColor = 0;
    private String fuelType;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up_vehicle);
        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);

        if (savedInstanceState == null) {
            authModel = ViewModelProviders.of(this).get(AuthModel.class);
        }

        //createDialog();

        Bundle extras = getBundle();

        String userId = extras.getString(DatabaseValue.USER_ID.name());
        Timber.i("userId: %s", userId);

        if (TextUtils.isEmpty(userId)) {
            dismissDialog();
            return;
        }


        String[] fuelTypeArray = {"GASOLINA", "DIESEL"};
        fuelTypeSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, fuelTypeArray));

        fuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                fuelType = fuelTypeArray[position];
                Toast.makeText(VehicleSignUp.this,"position: "+ position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        authModel.getUser().observe(this, this::onReadPerson);

    }

    private void createDialog() {
        dialog = new ProgressDialog(this);
        dialog.setText("Aguarde...");
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void onReadPerson(Person person) {
        if (person != null) {

            Timber.d("loaded person: %s", person);

            this.person = person;
        } else {
            this.finish();
        }
    }

    @OnClick(R.id.vehicle_sign_up_color)
    public void openColorPicker(View v){
        ArrayList<Integer> closestColorsList = new ArrayList<>();

        for (Integer in :
                getResources().getIntArray(R.array.color_list)) {
            closestColorsList.add(in);
        }

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                ColorPickerDialog.SELECTION_SINGLE,
                closestColorsList,
                3, // Number of columns
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
            @Override
            public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                Timber.d("selected colors %s", mSelectedColors);
                selectedColor = mSelectedColors.get(0);
            }
            @Override
            public void onDismiss() {
            }
        });

        dialog.show(getFragmentManager(), "some_tag");
    }


    private void onLoadPerson(Response<Person> response) {
        Timber.d("resposta: %s", response);

        if (response == null) {
            return;
        }

        switch (response.getRequestResult()) {
            case SUCCESSFULL:
                person = response.getData();
                person.setId(response.getKey());
                dismissDialog();
                break;

            case ERR_UNKNOWN:
                dismissDialog();
                Toast.makeText(this, "Erro desconhecido", Toast.LENGTH_SHORT).show();
                break;

            case ERR_NETWORK:
                dismissDialog();
                Toast.makeText(this, "Erro de Rede", Toast.LENGTH_SHORT).show();
                break;

            case ERR_OPERATION_CANCELED:
                dismissDialog();
                Toast.makeText(this, "Erro Operacao Cancelada", Toast.LENGTH_SHORT).show();
                break;

            default:
                dismissDialog();
                Toast.makeText(this, "Erro desconhecido", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @OnClick(R.id.vehicle_sign_up_submit_btn)
    public void savaDriverData(View view) {
        validator.validate();
    }


    @Override
    public void onValidationSucceeded() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMaker(getText(manufacturer));
        vehicle.setModel(getText(model));
        vehicle.setLot(Integer.parseInt(getText(available_seats)));
        vehicle.setYear(Integer.parseInt(getText(year)));
        vehicle.setLicencePlate(getText(licencePlate));
        vehicle.setColor(selectedColor);
        vehicle.setFuelType(fuelType);
        vehicle.setNumberOfCylinders(getNumber(numberOfCylinders));

        DriverLicence licence = new DriverLicence();
        licence.setNumber(getText(driverLicence));

        Timber.d("driver lincence: %s", licence);



        updateUser = authModel.updateDriverData(vehicle, licence);
        createDialog();
        updateUser.observe(this, this::onUpdateUser);


    }

    private void onUpdateUser(AuthService.AuthResult authResult) {
        if (authResult == null) {
            return;
        }

        if (authResult == AuthService.AuthResult.USER_UPDATED) {
            dismissDialog();
            startMyActivity(ClientMapActivity.class);

        } else {
            dismissDialog();
            Toasty.error(this, "could not update user").show();
        }


    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();

            // Display error messages ;)
            if (view instanceof EditText) {
                String message = this.getString(R.string.cannot_be_empty);
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, R.string.cannot_be_empty, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }


}
