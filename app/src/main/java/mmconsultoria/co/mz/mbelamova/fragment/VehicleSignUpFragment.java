package mmconsultoria.co.mz.mbelamova.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.BindView;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.activity.DriverMapsActivity;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.model.BaseFragment;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Vehicle;
import mmconsultoria.co.mz.mbelamova.view_model.AuthModel;


public class VehicleSignUpFragment extends BaseFragment {

    private String userid;
    @NotEmpty
    @BindView(R.id.vehicle_sign_up_manufacturer)
    public EditText manufacturer;
    @BindView(R.id.vehicle_sign_up_model)
    @NotEmpty
    public EditText model;
    @BindView(R.id.vehicle_sign_up_color)
    @NotEmpty
    public EditText color;
    @NotEmpty
    @BindView(R.id.vehicle_sign_up_licence_plate)
    public EditText licencePlate;
    @BindView(R.id.vehicle_sign_up_year)
    @NotEmpty
    public EditText year;
    @NotEmpty
    @BindView(R.id.vehicle_sign_up_available_seats)
    public EditText available_seats;
    private Spinner licence_code_spinner;


    private Validator validator;
    private AuthModel authModel;
    private Person person;

    public static VehicleSignUpFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(DatabaseValue.USER_ID.name(), userId);
        VehicleSignUpFragment fragment = new VehicleSignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_vehicle, container, false);


        licence_code_spinner = view.findViewById(R.id.licence_code_spinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.licence_code, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        licence_code_spinner.setAdapter(arrayAdapter);
        licence_code_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        view.findViewById(R.id.driver_data_back_btn).setOnClickListener(v -> getActivity().getSupportFragmentManager().
//                beginTransaction().
//                replace(R.id.login_container, new SignUpFragment()).
//                commit());
        view.findViewById(R.id.vehicle_sign_up_submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setText("Aguarde");
                progressDialog.show();

                new Handler().postDelayed(() -> {
                    startActivity(DriverMapsActivity.class);
                }, 2500);
            }
        });

        return view;
    }

    private Vehicle createVehicle() {
        return null;
    }

}
