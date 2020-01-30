package mmconsultoria.co.mz.mbelamova.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.adapter.DriversListAdapter;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.model.AvailableRide;
import mmconsultoria.co.mz.mbelamova.model.FireFunctions;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Place;
import mmconsultoria.co.mz.mbelamova.model.Trip;
import mmconsultoria.co.mz.mbelamova.view_model.MapViewModel;
import timber.log.Timber;



public class DriversListBottomSheet extends BottomSheetDialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnFocusChangeListener {


    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;


    private View view;
    private final String TAG = "DriversListBottomSheet";
    private GoogleApiClient mGoogleApiClient;

    private String currentFoncus;
    private boolean endFocu = false;
    private EditText mAutocompleteView_End;
    private MapViewModel mapModel;

    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<Person> listaMoto = new ArrayList<>();
    DriversListAdapter adapter;
    private Person currentPerson;
    private LiveData<Response<String>> requestRide;
    private Trip currentTrip;
    private boolean fromTrip = true;
    private ProgressDialog dialog;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentPerson = getArguments().getParcelable(DatabaseValue.PERSON.name());
            currentTrip = getArguments().getParcelable(DatabaseValue.TRIP.name());

            Timber.d("person %s and trip %s",currentPerson,currentTrip);

            if (currentTrip == null)
                fromTrip = false;
        }
    }

    public static DriversListBottomSheet fromPerson(Person person) {
        Timber.d("raw person: %s", person);

        Bundle args = new Bundle();
        args.putParcelable(DatabaseValue.PERSON.name(), person);
        DriversListBottomSheet fragment = new DriversListBottomSheet();
        fragment.setArguments(args);

        return fragment;
    }

    public static DriversListBottomSheet fromTrip(Trip trip) {

        Timber.d("raw trip: %s", trip);

        Bundle args = new Bundle();
        args.putParcelable(DatabaseValue.TRIP.name(), trip);
        DriversListBottomSheet fragment = new DriversListBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my, container, false);


        recyclerView = view.findViewById(R.id.driver_list_recycler_bottonshit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DriversListAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (fromTrip) {
            adapter.setOnDriverClick(this::handleFromSearch);
        } else {
            adapter.setOnDriverClick(this::handleFromNotification);
        }


        dialog = createDialog();
        searchRiders();

        return view;


    }

    private void fillListFromNotification() {
        mapModel.getDriversList().observe(this, this::populateList);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    private void handleFromSearch(AvailableRide ride) {
        currentTrip.setDriverName(ride.getRide().getDriverName());
        currentTrip.setDriverId(ride.getRide().getDriverId());
        currentTrip.setDistanceInKilo(ride.getDistance());
        currentTrip.setPrice(ride.getPrice());
        currentTrip.setStartPoint(ride.getStartPoint());
        currentTrip.setEndPoint(ride.getEndPoint());
        requestRide = mapModel.requestTrip(currentTrip, ride.getRide().getId());
        requestRide.observe(DriversListBottomSheet.this, DriversListBottomSheet.this::onRideRequest);
    }


    private void handleFromNotification(AvailableRide ride) {
        Trip trip = Trip.builder(currentPerson, ride.getStartPoint(), ride.getEndPoint())
                .startTime(ride.getRide().getStartTime())
                .distanceInKillo(ride.getDistance())
                .price(ride.getPrice())
                .driverName(ride.getRide().getDriverName())
                .driverId(ride.getRide().getDriverId())
                .build();

        requestRide = mapModel.requestTrip(trip, ride.getRide().getId());
        requestRide.observe(DriversListBottomSheet.this, DriversListBottomSheet.this::onRideRequest);
    }

    private ProgressDialog createDialog() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setText("Aguarde...");
        dialog.show();
        return dialog;
    }

    private void searchRiders() {

        if (currentTrip != null)
            FireFunctions.init().searchDrivers(currentTrip).observe(this, this::onSearchRidersResponse);
        else Timber.d("the Trip is null");

    }

    private void onSearchRidersResponse(Response<List<AvailableRide>> response) {
        if (response == null) {
            emptyList();
            return;
        }

        if (response.getRequestResult() == RequestResult.ERR_UNKNOWN) {
            emptyList();
            return;
        }



        populateList(response.getData());
    }

    private void populateList(List<AvailableRide> data) {
        dialog.dismiss();

        if (data == null || data.isEmpty()) {
            Toasty.info(getActivity(), "nao ha viagens disponiveis").show();
            this.dismiss();
        }

        for (AvailableRide ride :
                data) {
            adapter.add(ride);
        }

        adapter.notifyDataSetChanged();
    }

    private void emptyList() {
        dialog.dismiss();
    }

    private void onRideRequest(Response<String> stringResponse) {

        switch (stringResponse.getRequestResult()) {
            case SUCCESSFULL:
                Toasty.success(getActivity(), "Viagem Agendada").show();
                break;
            default:
                Toasty.error(getActivity(), "Houve um erro inesperado tente novamente").show();
                break;
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapModel = ViewModelProviders.of(getActivity())
                .get(MapViewModel.class);

        if (!fromTrip)
            fillListFromNotification();

        Timber.d("activity created: %s", mapModel);
    }

    private void requestSuccess(RequestResult requestResult) {
        Timber.i(requestResult.name());
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.v("Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.v("Connection Suspended");
        Timber.v(String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.v("Connection Failed");
        Timber.v(String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {


    }


    public double valueToPay(double volume, double fuelPrice, double distance) {
        return volume * fuelPrice * distance + (volume * fuelPrice * distance * 0.15);
    }



    private LatLng createPlace(Place latLng) {
        if (latLng == null)
            return null;
        return new LatLng(latLng.getLatitude(), latLng.getLongitude());
    }
}
