package mmconsultoria.co.mz.mbelamova.fragment;


import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.activity.ClientMapActivity;
import mmconsultoria.co.mz.mbelamova.activity.DriverMapsActivity;
import mmconsultoria.co.mz.mbelamova.adapter.PlacesAutoCompleteAdapter;
import mmconsultoria.co.mz.mbelamova.adapter.RecyclerItemClickListener;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.model.BaseFragment;
import mmconsultoria.co.mz.mbelamova.model.Controller;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Place;
import mmconsultoria.co.mz.mbelamova.model.Trip;
import mmconsultoria.co.mz.mbelamova.view_model.MapViewModel;
import timber.log.Timber;

import static mmconsultoria.co.mz.mbelamova.model.Controller.FOCUS_END;
import static mmconsultoria.co.mz.mbelamova.model.Controller.FOCUS_START;

/**
 * A simple {@link Fragment} subclass.
 */
public class TravelDataDriver extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, View.OnFocusChangeListener {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private Spinner numberOfSeatsSpinner;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    EditText start_EDT = null;
    public static EditText end_EDT = null;

    private View view;
    private GoogleApiClient mGoogleApiClient;
    private EditText mAutocompleteView_Start;
    private ListView mRecyclerViewHome;
    private Person currentPerson;
    private ArrayAdapter<Integer> adapter;
    private int mHour;
    private int mMinute;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Place startPoint, endPoint;

    public static String getCurrentFoncus() {
        return currentFoncus;
    }

    public static void setCurrentFoncus(String currentFoncus) {
        TravelDataDriver.currentFoncus = currentFoncus;
    }

    public static String currentFoncus;
    private boolean endFocu = false;
    private EditText mAutocompleteView_End;
    private ImageView clean_StartF;
    private ImageView clean_EndF;
    private TextView startTime, startDate;
    private ImageView go_back;

    private ImageButton partida_mapa;
    private ImageButton destino_mapa;
    private TextView feito;
    private static int PLACE_PICKER_REQUEST = 1;
    private MapViewModel mapModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentPerson = getArguments().getParcelable(DatabaseValue.PERSON.name());
        }
    }

    private void minimize() {
        getChildFragmentManager().beginTransaction().detach(this).commitNow();
    }

    public static TravelDataDriver fromPerson(Person person) {

        Bundle args = new Bundle();
        args.putParcelable(DatabaseValue.PERSON.name(), person);
        TravelDataDriver fragment = new TravelDataDriver();
        fragment.setArguments(args);
        return fragment;
    }

    public TravelDataDriver() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        buildGoogleApiClient();
        view = inflater.inflate(R.layout.fragment_dados_da_viagem, container, false);
        mAutocompleteView_Start = view.findViewById(R.id.startEDT);
        mAutocompleteView_End = view.findViewById(R.id.endEDT);
        numberOfSeatsSpinner = view.findViewById(R.id.travel_data_number_of_available_seats);

        view.findViewById(R.id.travel_data_schedule).setOnClickListener(this::scheduleRide);
        view.findViewById(R.id.travel_data_search).setOnClickListener(this::searchRide);


        if (getActivity() instanceof DriverMapsActivity)
            fillSpinner(numberOfSeatsSpinner);
        else hidePassengersInfo(numberOfSeatsSpinner, view);


        partida_mapa = view.findViewById(R.id.botao_mapa_partida);
        destino_mapa = view.findViewById(R.id.botao_mapa_destino);


        // listnear();

        start_EDT = view.findViewById(R.id.startEDT);
        end_EDT = view.findViewById(R.id.endEDT);
        start_EDT.setOnFocusChangeListener(this::onFocusChange);
        end_EDT.setOnFocusChangeListener(this::onFocusChange);

        startDate = view.findViewById(R.id.travel_data_date);
        startDate.setOnClickListener(this::updateDate);
        startTime = view.findViewById(R.id.travel_data_time);
        startTime.setOnClickListener(this::onUpdateTime);
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        startTime.setText(mHour + ":" + mMinute);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        startDate.setText(mYear + "/" + mMonth + "/" + mDay);

        clean_StartF = view.findViewById(R.id.clean_startEDT);
        clean_StartF.setOnClickListener(v -> start_EDT.setText(""));
        clean_EndF = view.findViewById(R.id.clean_endEDT);
        clean_EndF.setOnClickListener(v -> end_EDT.setText(""));

        view.findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeFrag();
            }
        });

        partida_mapa.setOnClickListener(v -> {
            Controller.getInstance().setCurrentFocus(FOCUS_START);
            minimize();
        });

        destino_mapa.setOnClickListener(v -> {
            Controller.getInstance().setCurrentFocus(FOCUS_END);
            minimize();
        });
        Timber.d("estado " + start_EDT.getText().length() + " outro: " + Controller.getInstance().getStart());


        prencherLista();
        return view;
    }

    private void onUpdateTime(View view) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {

            startTime.setText(hourOfDay + ":" + minute);
        }, mHour, mMinute, true);
        dialog.show();
    }

    private void updateDate(View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), (view1, year, month, dayOfMonth) -> {
            startDate.setText(year + "/" + month + "/" + dayOfMonth);
        }, mYear, mMonth, mDay);
        dialog.show();
    }

    private void hidePassengersInfo(Spinner numberOfSeatsSpinner, View view) {
        numberOfSeatsSpinner.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.numberOfAvailableSeats).setVisibility(View.INVISIBLE);
    }

    private void fillSpinner(Spinner numberOfSeatsSpinner) {

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        int lot = currentPerson.getDriverData().getVehicle().getLot();
        Controller.getInstance().setNumberOfAvailableSeats(lot);
        numberOfSeatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Controller.getInstance().setNumberOfAvailableSeats(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        for (int i = 1; i < lot; i++) adapter.add(i);
        numberOfSeatsSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mapModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void prencherLista() {


//        if (currentFoncus.equals("edt1"))
//            start_EDT.setText(places.get(0).getName());
//            Controller.init().setStartName(places.get(0).getName() + "");
//            Controller.init().setStart(places.get(0).getLatLng());
//

// ListView HOme,Work
        mRecyclerViewHome = (ListView) view.findViewById(R.id.listViewHome);
        String opcoes[] = {"*Selecionar no Mapa"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.lista_view,
                opcoes);
        mRecyclerViewHome.setAdapter(adapter);
        mRecyclerViewHome.setOnItemClickListener((parenty, viewCliked, position, id) -> {


            TextView textView = (TextView) viewCliked;
            try {
                if (currentFoncus.equals(FOCUS_START)) {
                    endFocu = false;
                    Timber.d("1 Fase");
                    if (((TextView) viewCliked).getText().toString().equals("* Casa")) {
                        Timber.d("Clicado: %s", ((TextView) viewCliked).getText().toString());
                        Timber.d("2 Fase");
                        start_EDT.setText("casa");
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                    if (((TextView) viewCliked).getText().toString().equals("Trabalho")) {
                        start_EDT.setText("Trabalho");
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                    if (((TextView) viewCliked).getText().toString().equals("Local actual")) {
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();
                        start_EDT.setText("Local actual");
                    }
                    if (((TextView) viewCliked).getText().toString().equals("*Selecionar no Mapa")) {
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                } else if (currentFoncus.equals(FOCUS_END)) {

                    if (((TextView) viewCliked).getText().toString().equals("Casa")) {
                        end_EDT.setText("casa");
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                    if (((TextView) viewCliked).getText().toString().equals("Trabalho")) {
                        end_EDT.setText("Trabalho");
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                    if (((TextView) viewCliked).getText().toString().equals("Local actual")) {
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();
                        end_EDT.setText("Local actual");
                    }
                    if (((TextView) viewCliked).getText().toString().equals("*Selecionar no Mapa")) {
                        Toast.makeText(getActivity(), ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();

//                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//                        try {
//
//                            startActivityForResult(builder.build(getActivity()),Controller.init().getPLACE_PICKER_REQUEST());
//                            Toast.makeText(getActivity(), "pickup cod"+Controller.init().getPLACE_PICKER_REQUEST(), Toast.LENGTH_SHORT).show();
//                        } catch (GooglePlayServicesRepairableException e) {
//                            e.printStackTrace();
//                        } catch (GooglePlayServicesNotAvailableException e) {
//                            e.printStackTrace();
//                        }


                        ((ClientMapActivity) getActivity()).partida();


                        //  Marker pickUpMarker= mMap.addMarker(new MarkerOptions().position(currentPosition).title("PickUpLocation"));

                        // End marker

                        MarkerOptions options = new MarkerOptions();
                        // options.position(end);
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                        //mMap.addMarker(options);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        // listnear();


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("MZ")
                .build();
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.searchview_adapter,
                mGoogleApiClient, BOUNDS_INDIA, typeFilter);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);

        mAutocompleteView_Start.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
//                if(s.toString().equals("")){
//                    start_EDT.setText("Local actual");
//                    start_EDT.setBackgroundColor(Color.rgb(4,5,1));
//                }else
                Timber.d("isConnected: " + mGoogleApiClient.isConnected());
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {

                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {

                    Timber.e("Google API not connected");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }


        });

        mAutocompleteView_End.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
//                if(s.toString().equals("")){
//                    start_EDT.setText("Local actual");
//                    start_EDT.setBackgroundColor(Color.rgb(4,5,1));
//                }else
                Timber.d("isConnected: %s", mGoogleApiClient.isConnected());
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {

                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(getActivity(), "Google API not connected", Toast.LENGTH_SHORT).show();
                    Timber.e("Google API not connected");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }


        });


        try {
            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity(), (view, position) -> {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        String placeId = null;
                        try {
                            placeId = String.valueOf(item.placeId);
                            Timber.i("Autocomplete item: %s", item.description);


                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */


                            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                    .getPlaceById(mGoogleApiClient, placeId);
                            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(PlaceBuffer places) {
                                    if (places.getCount() == 1) {
                                        //Do the things here on Click.....
                                        Controller instance1 = Controller.getInstance();
                                        if (currentFoncus.equals(FOCUS_START)) {
                                            start_EDT.setText(places.get(0).getName());
                                            instance1.setStartName(places.get(0).getName() + "");
                                            instance1.setStart(places.get(0).getLatLng());
                                            Timber.d("start: %s", places.get(0).getLatLng());
                                            startPoint = createPlace(places.get(0).getLatLng()).setName(places.get(0).getName().toString());

                                            //Toast.makeText(getActivity(),String.valueOf(places.get(0).getLatLng()) +"|"+places.get(0).getAddress(),Toast.LENGTH_SHORT).show();
                                        }
                                        if (currentFoncus.equals(FOCUS_END)) {
                                            end_EDT.setText(places.get(0).getName());
                                            instance1.setDestinationName(places.get(0).getName() + "");
                                            instance1.setDestination(places.get(0).getLatLng());
                                            endPoint = createPlace(places.get(0).getLatLng()).setName(places.get(0).getName().toString());
                                            Timber.d("end: %s", places.get(0).getLatLng());

                                            //                                        Toast.makeText(getActivity(),String.valueOf(places.get(0).getLatLng()) +"|"+places.get(0).getAddress(),Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "OOPs!!! Something went wrong...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    })
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Antigo
//        ListView listView=(ListView) view.findViewById(R.id.list_view);
//        String opcoes[]={"-> Casa","-> Trabalho","-> Selecionar no Mapa"};
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.searchview_adapter,
//                opcoes);
//        listView.setAdapter(adapter);

    }

    private void scheduleRide(View v) {
        if(startPoint == null || start_EDT.getText().toString().trim().length() == 0){
            start_EDT.setError("Local invalido");
            return;
        }

        if( endPoint == null || end_EDT.getText().toString().trim().length() == 0){
            end_EDT.setError("Local invalido");
            return;
        }

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setText("Aguarde...");
        dialog.show();
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
        long time = calendar.getTimeInMillis();

        Controller instance = Controller.getInstance();

        if (getActivity() instanceof ClientMapActivity) {

            Trip buiid = Trip.
                    builder(currentPerson,
                            createPlace(instance.getStart())
                                    .setName(instance.
                                            getStartName()),
                            createPlace(instance
                                    .getDestination())
                                    .setName(instance
                                            .getDestinationName()))
                    .startTime(time)
                    .build();

            Timber.d("trip: %s", buiid);


            mapModel.scheduleTrip(buiid).observe(this, observer -> {
                dialog.dismiss();
                if (observer.getRequestResult() == RequestResult.SUCCESSFULL) {
                    Toasty.info(getActivity(), "Viagem AGENDADA").show();
                }
            });

        } else {

            ((DriverMapsActivity) getActivity()).getDirection(startPoint, endPoint)
                    .subscribeOn(Schedulers.io())
                    .subscribe(success -> {
                        Timber.i("route ");

                        success.setStartTime(time);
                        mapModel.createRide(success)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(onSuccess -> {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.trip_scheduled), Toast.LENGTH_SHORT).show();
                                    Timber.i("viagem agendada");
                                    closeFrag();
                                }, throwable -> {
                                    Timber.e(throwable);
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.trip_scheduled_failed), Toast.LENGTH_SHORT).show();
                                });


                    }, throwable -> {
                        if (throwable instanceof org.json.JSONException) {
                            Toast.makeText(getActivity(), "Local Inalcancavel", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Nao foi possivel desenhar a rota", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    });
        }
    }


    private void searchRide(View v) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setText("Aguarde...");
        dialog.show();
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
        long time = calendar.getTimeInMillis();


        Trip buiid = Trip.
                builder(currentPerson,
                        startPoint, endPoint)
                .startTime(time)
                .build();


        Timber.i("Thread: %s , Raw Trip: %s", Thread.currentThread().getName(), buiid);

        if (currentPerson.getDriverData() == null && getActivity() instanceof DriverMapsActivity) {
            Toasty.info(getActivity(), "Sem dados de motorista").show();
            return;
        }
        DriversListBottomSheet driveresListBottomSheet = DriversListBottomSheet.fromTrip(buiid);

        Timber.d("Fragment status: %s", getLifecycle().getCurrentState());
        if (getActivity() != null) {

            driveresListBottomSheet.show(getActivity().getSupportFragmentManager(), "Exemplo de Bottom Sheet");
            closeFrag();
        }


        dialog.dismiss();

    }

    private void closeFrag() {
        Controller instance = Controller.getInstance();
        instance.setCurrentFocus("");
        instance.setStart(null);
        instance.setStartName(null);
        instance.setDestination(null);
        instance.setDestinationName(null);
        getActivity().onBackPressed();
    }


    private mmconsultoria.co.mz.mbelamova.model.Place createPlace(LatLng latLng) {
        if (latLng == null)
            return null;
        return mmconsultoria.co.mz.mbelamova.model.Place.build(latLng.latitude, latLng.longitude).build();
    }


    private LatLng toLatLng(Location location) {
        if (location == null)
            return null;
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        Controller instance = Controller.getInstance();

        if (instance.getStart() != null) {
            startPoint = createPlace(instance.getStart()).setName(instance.getStartName());
            if (!TextUtils.isEmpty(instance.getStartName())) {
                start_EDT.setText(instance.getStartName());
            } else {
                start_EDT.setText(String.format("%s,%s", instance.getStart().latitude, instance.getStart().longitude));
            }
        } else {
            if (start_EDT.getText().length() == 0) {
                start_EDT.setText("Local Actual");
                Controller.getInstance().setStart(Controller.getInstance().getCurrentLocation());
                view.setNextFocusDownId(end_EDT.getId());
                startPoint = createPlace(instance.getStart());
            }
        }

        if (instance.getDestination() != null) {
            endPoint = createPlace(instance.getDestination()).setName(instance.getDestinationName());
            if (!TextUtils.isEmpty(instance.getDestinationName())) {
                end_EDT.setText(instance.getDestinationName());
            } else {
                end_EDT.setText(String.format("%s,%s", instance.getDestination().latitude, instance.getDestination().longitude));
            }
        }

        mGoogleApiClient.connect();

    }

    public void clearInputFialds(View view) {
        start_EDT = view.findViewById(R.id.startEDT);
        start_EDT.setText("");

        end_EDT = view.findViewById(R.id.endEDT);
        end_EDT.setText("");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == start_EDT && hasFocus) {
            currentFoncus = FOCUS_START;
            Timber.d(" view %s, focus state: %s", ((EditText) v).getText().toString(), currentFoncus);
        } else if (v == end_EDT && hasFocus) {
            currentFoncus = FOCUS_END;
            Timber.d(" view %s, focus state: %s", ((EditText) v).getText().toString(), currentFoncus);
        }

        Controller.getInstance().setCurrentFocus(currentFoncus);
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


    public double valueToPay(double volume, double fuelPricePerLIter, double tripDistance) {
        return volume * fuelPricePerLIter * tripDistance + (volume * fuelPricePerLIter * tripDistance * 0.15);
    }


}
