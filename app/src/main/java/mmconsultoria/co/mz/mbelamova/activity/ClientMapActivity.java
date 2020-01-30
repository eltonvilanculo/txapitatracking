package mmconsultoria.co.mz.mbelamova.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Routing;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.util.GeoUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import es.dmoral.toasty.Toasty;
import io.reactivex.Single;
import mmconsultoria.co.mz.mbelamova.Common.Common;
import mmconsultoria.co.mz.mbelamova.Common.Util;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.adapter.LatLngInterpolator;
import mmconsultoria.co.mz.mbelamova.adapter.MarkerAnimation;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.fragment.DriversListBottomSheet;
import mmconsultoria.co.mz.mbelamova.fragment.TravelDataDriver;
import mmconsultoria.co.mz.mbelamova.fragment.VehicleSignUp;
import mmconsultoria.co.mz.mbelamova.model.AvailableRide;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.Controller;
import mmconsultoria.co.mz.mbelamova.model.DriverLicence;
import mmconsultoria.co.mz.mbelamova.model.FireFunctions;
import mmconsultoria.co.mz.mbelamova.model.FireRequestResponse;
import mmconsultoria.co.mz.mbelamova.model.IGoogleAPI;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Review;
import mmconsultoria.co.mz.mbelamova.model.Trip;
import mmconsultoria.co.mz.mbelamova.model.TripStatus;
import mmconsultoria.co.mz.mbelamova.service.FirebaseNotificationService;
import mmconsultoria.co.mz.mbelamova.util.AppUtils;
import mmconsultoria.co.mz.mbelamova.view_model.MapViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static java.lang.Double.parseDouble;
import static mmconsultoria.co.mz.mbelamova.R2.id.frame_layout;
import static mmconsultoria.co.mz.mbelamova.model.FireRequestResponse.Type.MPESA;
import static mmconsultoria.co.mz.mbelamova.service.FirebaseNotificationService.NOTIFICATION_BUNDLE;
//import com.google.android.gms.location.places.Place;


public class ClientMapActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {


    private static final String TAG = "ClientMapActivity";

    private LatLng dummyLatlong = new LatLng(-25.9740854, 32.5710671);

    //Payment variables
    double saldoDisponivel;
    double saldoIntroduzido;

    private boolean hasDriverMetClient = false;

    //Permissoes
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //Constantes
    private static final float DEFAULT_ZOOM = 15f;
    private static final int RC_SIGN_IN = 9001;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));

    //Widget Vars
    private ImageView mGps;
    private DrawerLayout mDrawerLayout;
    private de.hdodenhof.circleimageview.CircleImageView mPerfilFoto;
    private ImageView navigation_menu;
    private CardView mSettings;
    private TextView nav_profile_name;
    private EditText editTextValorRec;
    private TextView txtSaldo;
    private Button btnRecarregar, btnDescarregar;
    private TextView pesquisa_Fiald;

    //Map Vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; // Voce me deu Problemas
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    DatabaseReference driver;
    GeoFire geoFire;
    Marker currentMarker;
    private LatLng start;
    protected LatLng end;

    //Bottom Sheet
    EditText start_EDT;
    EditText end_EDT;
    Button tracarRota_BT;
    private View view;


    //Dados google

    //private final String TAG =this.getClass().getSimpleName() ;


    private Polyline currentPolyline;

    // Animacao do carro
    private List<LatLng> polyLineList;
    private Marker carMarker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition, currentPosition;
    private int index, next;
    private Button tracarRota;
    private Button partida;
    private Button destino;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;
    private IGoogleAPI mService;
    private ProgressDialog progressDialog;
    private List<Polyline> polylines;
    private NavigationView navigationView;
    private Location currentLocation;
    private Object dataTranfer;

    double tripDistance;
    private double value;
    private double volume = 0.16;
    private double fuelPrice = 69;
    private ListView listView;
    private LatLng pickupLocation;
    private TextView duracaoViagem;
    private TextView distanciaViagem;
    private String duracao;
    private CardView detalhesViagemCardView;

//Inicializacao de variaves

    private ImageView mDriverProfileImage;

    private TextView mDriverName, mDriverPhone, mDriverCar;

    private RadioGroup mRadioGroup;
    private Boolean requestBol = false;
    private String requestService;
    private TextView nav_profile_balance;
    private MapViewModel mapModel;
    private Person currentPerson;
    private boolean isClientOnLift = false;
    private String phoneNumber;
    private double valorReccarga;
    private double auxBalance;

    private LocationCallback mLocationCallback;
    private Vector<DriverLicence> ver;
    private TextView valorRegarga;
    private double miniValueToPay;
    private Marker DriverMarker;
    private BroadcastReceiver broadcastReceiver;
    private LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response<String>> requestTripLiveData;
    private LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response> confirmTripLiveData;
    private LatLng lastDriverLocation = new LatLng(0, 0);
    private LatLng fashionLatLong;
    private LatLng edmLatLong;
    private LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response<String>> reviewResposeLiveData;
    private AlertDialog reviewDialog;
    private LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response<FireRequestResponse>> confirmTrip;
    private ImageView dragLocationButton;
    private Trip currentTrip;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundleExtra = intent.getBundleExtra(NOTIFICATION_BUNDLE);
        if (bundleExtra != null) {
            Timber.d("extras: %s", bundleExtra.keySet());

            String purpose = bundleExtra.getString("purpose");


            if (purpose.equals(FirebaseNotificationService.PORPOSE_TRIP_SCHEDULE)) {


                showDriversList(bundleExtra);
            }

            createdDialog(bundleExtra);
        }


    }

    public void createReviewDialog(String driverName, String rideId, String driverId) {
        View view = LayoutInflater.from(this).inflate(R.layout.review_dialog, null, false);
        TextView name = view.findViewById(R.id.review_edit_reviewed_name);
        EditText comment = view.findViewById(R.id.review_edit_comment);
        EditText score = view.findViewById(R.id.review_edit_score);

        name.setText(driverName);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Commentar Viagem");
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Review review = new Review(getText(comment), rideId, getNumber(score).intValue());
                reviewResposeLiveData = mapModel.reviewDriver(review, driverId);
                reviewResposeLiveData.observe(ClientMapActivity.this, ClientMapActivity.this::onReviewResponse);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        reviewDialog = builder.create();

        reviewDialog.show();
    }

    private void onReviewResponse(mmconsultoria.co.mz.mbelamova.fragment.Response<String> stringResponse) {
        if (stringResponse.getRequestResult() == RequestResult.SUCCESSFULL) {
            dismissReviewDialog();
        }
    }

    private void dismissReviewDialog() {
        if (reviewDialog != null)
            reviewDialog.dismiss();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_map);


        //SubData data = new SubData();
        //VolleyRequestQueuer.init(this).sendDataNotification("Titulo","Subject", data,currentPerson.getId());

        initViews();

        try {

            Toasty.info(this, "Cliente", Toast.LENGTH_SHORT).show();
            fashionLatLong = new LatLng(-25.9574704, 32.5632606);
            edmLatLong = new LatLng(-25.9677076, 32.5827401);


            getLocationPermission();
            // Abilitar Localizacao (GPS)
            acceptLocationPermission();
            buildGoogleApiClient();

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            //Navedation Drawer

            pesquisa_Fiald.setOnClickListener(v -> {
                mMap.setOnCameraMoveListener(this);
                mMap.setOnCameraIdleListener(this);

                TravelDataDriver travelDataDriver = TravelDataDriver.fromPerson(currentPerson);

                swapFragmentAndAddToBackStack(frame_layout, travelDataDriver, null, null);
//                DriveresListBottomSheet myFragment=new DriveresListBottomSheet();
//                myFragment.show(getSupportFragmentManager(),"Exemplo de Bottom Sheet");
            });

            mPerfilFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPerson != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString(DatabaseValue.USER_ID.name(), currentPerson.getId());
                        startActivity(VehicleSignUp.class, bundle, null);
                    }
                }
            });


            // customeRequest();

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {


                    for (Location location : locationResult.getLocations()) {

                        Timber.i("actual location: latitude = %s, longitue =%s", location.getLatitude(), location.getLongitude());
                        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                        String userId = FirebaseDatabase.getInstance().getReference("driversAvailable").getKey();
                        GeoFire geoFireAvailable = new GeoFire(refAvailable);
                        LatLng posiMto = new LatLng(location.getLatitude(), location.getLongitude());
                        mLastLocation = location;
                        Controller.getInstance().setCurrentLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

//                        getDriverLocation(createPlace(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())),"UH8w15G4NtMWtXQk08KqTzzQxdF2");


                        Timber.d("localActusl" + Controller.getInstance().getCurrentLocation());
                        showMarker(location);
                        geoFireAvailable.getLocation(userId, new com.firebase.geofire.LocationCallback() {
                            @Override
                            public void onLocationResult(String key, GeoLocation location) {

                                if (location != null) {
                                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                                } else {
                                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Timber.e(databaseError.toException());
                                System.err.println("There was an error getting the GeoFire location: " + databaseError);
                            }
                        });


                    }

                }


            };

            // Nevegation Drawer
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {


                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // set item as selected to persist highlight
                            menuItem.setChecked(false);
                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();

                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here
                            int id = menuItem.getItemId();

                            if (id == R.id.nav_dar_boleia) {
                                Intent homeIntent = new Intent(ClientMapActivity.this, DriverMapsActivity.class);
                                startActivity(homeIntent);
                                finish();


                            } else if (id == R.id.nav_definicoes) {
                                startMyActivity(SettingsActivity.class);
                            } else if (id == R.id.nav_promocao) {
                                Bundle bundle = new Bundle();
                                bundle.putString(DatabaseValue.USER_ID.name(), currentPerson.getId());
                                startActivity(PromoActivity.class, bundle, null);
                            } else if (id == R.id.nav_pagamento) {

                                View customView = getLayoutInflater().inflate(R.layout.recharge_dialog, null, false);
                                editTextValorRec = customView.findViewById(R.id.valor_a_recarregar);
                                btnDescarregar = customView.findViewById(R.id.redraw_button);
                                btnRecarregar = customView.findViewById(R.id.recharge_button);
                                TextView textPhoneNumber = customView.findViewById(R.id.dialog_phone_number_id);
                                textPhoneNumber.setText(currentPerson.getPhoneNumber());

                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ClientMapActivity.this);
                                //alertBuilder.setTitle("Recarga");
                                alertBuilder.setView(customView);
                                AlertDialog alertDialog = alertBuilder.create();
                                //saldoIntroduzido = Double.parseDouble(editTextValorRec.getText().toString());

                                alertDialog.show();

                                btnDescarregar.setOnClickListener(v -> {
                                    alertDialog.hide();

                                    String saldoTxt = editTextValorRec.getText().toString().trim();
                                    if (TextUtils.isEmpty(saldoTxt)) {
                                        Toasty.error(ClientMapActivity.this, "Introduza um valor ").show();
                                        alertDialog.dismiss();
                                        return;
                                    }
                                    saldoIntroduzido = Double.parseDouble(saldoTxt);
                                    Timber.d("saldo introduzido: %s", saldoDisponivel);

                                    FireFunctions.init().redrawMoney(saldoIntroduzido, MPESA).observe(ClientMapActivity.this, ClientMapActivity.this::handlePayment);

                                    alertDialog.dismiss();

                                });

                                btnRecarregar.setOnClickListener(v -> {
                                    alertDialog.hide();


                                    String saldoTxt = editTextValorRec.getText().toString().trim();
                                    if (TextUtils.isEmpty(saldoTxt)) {
                                        Toasty.error(ClientMapActivity.this, "Introduza um valor ").show();
                                        alertDialog.dismiss();
                                        return;
                                    }
                                    saldoIntroduzido = Double.parseDouble(saldoTxt);
                                    Timber.d("saldo introduzido: %s", saldoDisponivel);

                                    FireFunctions.init().makeDeposit(currentPerson, saldoIntroduzido, MPESA)
                                            .observe(ClientMapActivity.this, ClientMapActivity.this::handlePayment);

                                    alertDialog.dismiss();

                                });
                                /*alertDialog.hide();*/
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                            } else if (id == R.id.nav_historico) {

                                Bundle bundle = new Bundle();
                                bundle.putString(DatabaseValue.USER_ID.name(), currentPerson.getId());
                                startActivity(StatsActivity.class, bundle, null);
                            }

                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_client);
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        }
                    });


            tracarRota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (internetVerification()) {

                        getDirection();
                        mMap.clear();

                        //customeRequest();

                    }


                }


                private boolean internetVerification() {
                    boolean aux = false;
                    if (Util.Operations.isOnline(ClientMapActivity.this)) {
                        aux = true;
                    } else {
                        aux = false;
                        snackBar(findViewById(R.id.drawer_layout_client), "Sem Conexão a Internet");
                    }
                    return aux;
                }
            });


            destino.setOnClickListener(v -> {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(ClientMapActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }


                //  Marker pickUpMarker= mMap.addMarker(new MarkerOptions().position(currentPosition).title("PickUpLocation"));

                // End marker

                MarkerOptions options = new MarkerOptions();
                options.position(end);
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                mMap.addMarker(options);

            });


            polyLineList = new ArrayList<>();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            mService = Common.geIGoogleAPI();

            mapModel = ViewModelProviders
                    .of(this)
                    .get(MapViewModel.class);

            mapModel.getCurrentUser();
            updatePersonInfo(mapModel.getUser().getValue());

            mapModel.getUser().observe(this, this::updatePersonInfo);

            readNotifications();
            setOnTripUpdatedListener(this::onNotificationTripUpdated);

        } catch (Exception e) {
            Timber.e(e);
        }

        onNewIntent(getIntent());
    }

    private void handlePayment(mmconsultoria.co.mz.mbelamova.fragment.Response<FireRequestResponse> paymentResponseResponse) {
        Timber.d("handle payment: " + paymentResponseResponse);
        androidx.appcompat.app.AlertDialog.Builder mpesa = new androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Deposito Mpesa");

        if (paymentResponseResponse.getRequestResult() != RequestResult.SUCCESSFULL) {
            mpesa.setMessage("Deposito falhou").create().show();
            return;
        }

        FireRequestResponse data = paymentResponseResponse.getData();
        if (data.isSucessful()) {
            mpesa.setMessage("Deposito efectado com sucesso").create().show();

        } else {
            mpesa.setMessage("Deposito efectado sem sucesso").create().show();
        }
    }

    private void initViews() {
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(v -> {
            if (currentPerson != null) {
                startActivity(AccountActivity.class, null, currentPerson);
            }
        });
        nav_profile_name = headerView.findViewById(R.id.nome_textView);
        nav_profile_balance = headerView.findViewById(R.id.maps_header_balance);
        mPerfilFoto = headerView.findViewById(R.id.perfil_foto);

        navigationView = findViewById(R.id.nav_view);
        navigation_menu = findViewById(R.id.navigation_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout_client);

        pesquisa_Fiald = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);
        dragLocationButton = findViewById(R.id.drag_location_marker);
        tracarRota = findViewById(R.id.btn_get_direction);
        tracarRota.setVisibility(View.GONE);
        partida = findViewById(R.id.btnPartida);
        destino = findViewById(R.id.btnDestino);

        distanciaViagem = findViewById(R.id.distanciaTV);
        duracaoViagem = findViewById(R.id.duracaoTV);
        detalhesViagemCardView = findViewById(R.id.detalhesViagem);
        detalhesViagemCardView.setVisibility(View.GONE);

        //pagamento
        View customView = getLayoutInflater().inflate(R.layout.recharge_dialog, null, false);
        valorRegarga = customView.findViewById(R.id.valor_a_recarregar);
        btnRecarregar = customView.findViewById(R.id.recharge_button);

        registerBroadCast();

    }

    private void registerBroadCast() {

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION_CLIENT)) {
                    Bundle bundle = intent.getBundleExtra(NOTIFICATION_BUNDLE);
                    Timber.i("notification received");

                    if (bundle == null) {
                        Timber.i("No new notification or empty bundle");
                        return;
                    }

                    String sender = bundle.getString("rideId");

                    for (String key : bundle.keySet()) {
                        Timber.i("received data: %s", key);
                    }
                    Timber.i("received data - sender: %s", sender);


                    createdDialog(bundle);
                    //startActivity(NotificationActivity.class, bundle, null);


                }
            }
        };
        IntentFilter filter = new IntentFilter();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }


    private void readNotifications() {


        Trip currentTrip = (Trip) getParcelable();

        Timber.i("Current Trip: %s", currentTrip);

        if (currentTrip == null) {
            Timber.i("No Parcelable");
            return;
        }


        if (currentTrip.getStatus() == TripStatus.IN_PROGRESS) {
            // TODO: 5/4/2019 wait for Gimo
        }

    }

    private void onNotificationTripUpdated(String rideId, Trip trip) {
        Timber.d("rideId %s, trip: %s ", rideId, trip);

        trip.setRideId(rideId);

        if (TripStatus.FINISHED == trip.getStatus()) {
            createReviewDialog(trip.getDriverName(), rideId, trip.getDriverId());
            getDriverLocation(null, "", "");
        }


        if (TripStatus.DRIVER_CONFIRMED == trip.getStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Pedido de Viagem")
                    .setMessage("De: " + trip.getDriverName());
            builder.setTitle("Deseja confirmar viagem com");
            builder.setPositiveButton("SIM", (dialog, which) -> {
                confirmTrip(rideId);
            })
                    .setNegativeButton("NAO", (dialog, which) -> {
                        dialog.dismiss();
                        cancelTripClient(rideId, trip);
                    });
            builder.setCancelable(false);
            builder.show();
        }

        if (TripStatus.DRIVER_REJECTED == trip.getStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Pedido de Viagem")
                    .setMessage("De: " + trip.getDriverName());
            builder.setTitle("Pedido de Viagem rejeitado")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    }).setCancelable(false).show();

        }

        if (TripStatus.DRIVER_ARRIVED == trip.getStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Pedido de Viagem")
                    .setMessage("De: " + trip.getDriverName());
            builder.setTitle("O motorista chegou!!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();

            getDriverLocation(trip.startPoint(), trip.getDriverId(), trip.getDriverName());
        }

        if (TripStatus.IN_PROGRESS == trip.getStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Pedido de Viagem")
                    .setMessage("De: " + trip.getDriverName());
            builder.setTitle("Viagem em Andamento")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();

            saveTripToLocalStorage(trip);
            getDriverLocation(trip.startPoint(), trip.getDriverId(), trip.getDriverName());
        }


    }


    private void onRequestTripResult(mmconsultoria.co.mz.mbelamova.fragment.Response<String> stringResponse) {
        switch (stringResponse.getRequestResult()) {
            case SUCCESSFULL:
                Toasty.success(this, "Viagem Agendada").show();
                break;
            case ERR_UNKNOWN:
                Toasty.error(this, "Viagem Nao enviada").show();
                break;
            case NOTIFICATION_NOT_SENT:
                Toasty.error(this, "Notificacao Nao enviada").show();
                break;
        }

    }


    public void partida() {
        //Place Picker
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), Controller.getInstance().getPLACE_PICKER_REQUEST());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        // Start marker
//        MarkerOptions options = new MarkerOptions();
//        options.position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
//        options.title("Local de Partida");
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
//        mMap.addMarker(options);
//
//
//         Marker pickUpMarker= mMap.addMarker(new MarkerOptions().position(currentPosition).title("PickUpLocation"));
//

    }

    private void snackBar(View viewById, String s) {
        Snackbar.make(viewById, s, Snackbar.LENGTH_SHORT).show();
    }


    private void listnear() {
//      listView = (ListView) findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewCliked, int position, long id) {
                TextView textView = (TextView) viewCliked;
                // Toast.makeText(this, "Clicou a pos:" + position + "Conteudo:" + ((TextView) viewCliked).getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void route() {
        start = currentPosition;
        Log.d(TAG, "current position: " + currentPosition);
//
//        Log.d(TAG, "start: "+start);
//        Log.d(TAG, "end: "+end);

        if (start == null || end == null) {
            if (start == null) {
//                if(starting.getText().length()>0)
//                {
//                    starting.setError("Choose location build dropdown.");
//                }
//                else
//                {
//                    Toast.makeText(this,"Please choose a starting point.",Toast.LENGTH_SHORT).show();
//                }
            }
            if (end == null) {
                Toast.makeText(this, "End is empty", Toast.LENGTH_SHORT).show();
//                if(destination.getText().length()>0)
//                {
//                    destination.setError("Choose location build dropdown.");
//                }
//                else
//                {
//                    Toast.makeText(this,"Please choose a destination.",Toast.LENGTH_SHORT).show();
//                }
            }
        } else {
            progressDialog = ProgressDialog.show(ClientMapActivity.this, "Por favor Aguarde.",
                    "Processando a Rota.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    //.withListener(this)
                    .alternativeRoutes(false)// depois mudar
                    .waypoints(start, end)
                    .key(getResources().getString(R.string.google_maps_key))
                    .build();
            routing.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapModel.getLastTrip().observe(this, this::onLoadLastTrip);
    }

    private void onLoadLastTrip(mmconsultoria.co.mz.mbelamova.fragment.Response<Trip> tripResponse) {
        if (tripResponse.getRequestResult() == RequestResult.SUCCESSFULL) {
            currentTrip = tripResponse.getData();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "LocationChanging...", Toast.LENGTH_SHORT).show();
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mLastLocation = location;


        if (isClientOnLift) {
            return;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f));


    }


    public void cleanDestinoFild(View view) {
        TextView destinoFild = findViewById(R.id.input_search);
        destinoFild.setText("");
    }

    public void destinoPicker() {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(Controller.getInstance().getCurrentLocation())
                .title("Destino: ")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker));

        // markerOptions.draggable(true);

        markerOptions.draggable(true);


        tracarRota.setVisibility(View.VISIBLE);
        tracarRota.setText("Selecionar");
        tracarRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TravelDataDriver travelDataDriver = TravelDataDriver.fromPerson(currentPerson);

                swapFragmentAndAddToBackStack(frame_layout, travelDataDriver, null, null);
                tracarRota.setVisibility(View.GONE);
                mMap.clear();
                Toast.makeText(ClientMapActivity.this, markerOptions.getPosition() + "", Toast.LENGTH_SHORT).show();
            }
        });
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                markerOptions.getTitle();

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });
        //pickup Location

    }

    @Override
    public void onCameraIdle() {
        dragLocationButton.setVisibility(View.GONE);

        LatLng target = mMap.getCameraPosition().target;
        MarkerOptions options = new MarkerOptions()
                .position(target)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.add_marker));

        Timber.d("Current position: %s", target);

        Controller instance = Controller.getInstance();
        String currentFocus = instance.getCurrentFocus();



        Timber.d("current focus: %s", currentFocus);


        dragLocationButton.setVisibility(View.INVISIBLE);

        if (TextUtils.isEmpty(currentFocus)) {
            instance.setStart(null);
            instance.setDestinationName(null);
        }else{
            mMap.addMarker(options);
        }


        if (currentFocus.equals(Controller.FOCUS_START))
            instance.setStart(target);
        if (currentFocus.equals(Controller.FOCUS_END))
            instance.setDestination(target);


    }

    @Override
    public void onCameraMove() {

        mMap.clear();
        // display imageView


        Controller instance = Controller.getInstance();
        String currentFocus = instance.getCurrentFocus();


        Timber.d("current focus: %s", currentFocus);

        if (TextUtils.isEmpty(currentFocus)) {
            dragLocationButton.setVisibility(View.INVISIBLE);

        } else {
            dragLocationButton.setVisibility(View.VISIBLE);
        }


    }


    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < polyLineList.size() - 1) {
                startPosition = polyLineList.get(index);
                endPosition = polyLineList.get(next);

            }

            final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    v = valueAnimator.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f, 5f);
                    carMarker.setRotation(getBearing(startPosition, newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(15.5f)
                                    .build()
                    ));
                    valueAnimator.start();
                    handler.postDelayed(drawPathRunnable, 3000); // Problema, tinha que ser "this"
                }
            });
        }
    };

    private float getBearing(LatLng startPosition, LatLng endPosition) {
        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude - endPosition.longitude);

        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d(connectionResult.getErrorMessage());
    }


    public Single<Trip> getDirection() {
        mMap.clear();


        return Single.<Trip>create(emitter -> {
            currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            String requestApi = null;
            final Controller instance = Controller.getInstance();
            try {


                LatLng start = instance.getStart();
                String destination = instance.getDestinationName();

                Timber.i("routa:=> ,latitude: %s, longitude: %s, destination: %s", start.latitude, start.latitude, destination);

                pesquisa_Fiald.setText(destination);
                Log.d(TAG, "Destination: " + destination + "Start: " + start);
                requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "mode=driving&" +
                        "transit_routing_preference=less_driving&" +
                        "origin=" + start.latitude + "," + start.longitude + "&" +
                        "destination=" + destination + "&" +
                        "key=" + getResources().getString(R.string.google_direction_api);
                Log.d(TAG, "getDirection: " + requestApi);// Print Url for Debug
                mService.getPath(requestApi).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try {
                            Trip trip = Trip.empty();

                            //Timber.i("Response trip: %s", response.body());

                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            Timber.i("Response trip: %s", jsonObject);
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            //
                            //JSONObject rout = jsonArray.getJSONObject(0);
                            JSONArray legs = new JSONArray(jsonArray.getJSONObject(0).getString("legs"));
                            JSONObject legs_info = legs.getJSONObject(0);
                            JSONArray steps = legs_info.getJSONArray("steps");
                            duracao = legs_info.getJSONObject("duration").getString("text");
                            String distancia = legs_info.getJSONObject("distance").getString("text");
                            tripDistance = parseDouble(distancia.replace("km", "").replaceAll(" ", "").replace(",", ""));
                            Timber.d("thread: %s, Distancia da Viagem: %s", Thread.currentThread().getName(), tripDistance);

                            trip.setDistanceInKilo(tripDistance);

                            instance.setDistance(tripDistance);
                            trip.setStartPoint(createPlace(start));


                            Timber.d("duracao: %s", duracao);
                            Timber.d("distancia: %s", distancia);
                            runOnUiThread(() -> {
                                distanciaViagem.setText(distancia + "");
                                duracaoViagem.setText(duracao + "");
                            });

                            emitter.onSuccess(trip);
//                        tripDistance = Double.parseDouble(legs_info.getJSONObject("distance").getString("text"));

//                      //ProgresDialog
//                        progressDialog = ProgressDialog.show(ClientMapActivity.this, "Por favor Aguarde.",
//                                "Processando a Rota.", true,true);
//                        requestApi.build();
//                        routing.execute();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                polyLineList = decodePoly(polyline);
                                LatLng startPoint = polyLineList.get(0);
                                LatLng endpoint = polyLineList.get(polyLineList.size() - 1);
                                double geoFireDistance = GeoUtils.distance(startPoint.latitude, startPoint.longitude, endpoint.latitude, endpoint.longitude);

                                Timber.i("GeoFire distance: %s", geoFireDistance);

                                Timber.d("polylines: %s", polyLineList);
                                // boolean answer = isRideAcceptable(fashionLatLong,edmLatLong,polyLineList);

                                //Timber.d("isRideAcceptable: %s",answer);

                                //Adjusting Bounds

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList)
                                    builder.include(latLng);
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(R.color.rotaTracandoFundo);
                                polylineOptions.width(8);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyline = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(R.color.rotaTracandoSuperficie);
                                blackPolylineOptions.width(8);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                // mMap.addMarker(new MarkerOptions().position(instance.getStart()).title("Ponto de Busca").icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));

                                //polyLineList.get(polyLineList.size() - 1)
                                mMap.addMarker(new MarkerOptions()
                                        .position(instance.getDestination())
                                        .title("Destino: " + instance.getDestinationName() + " Duracao: " + duracao + " Distancia: " + distancia).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
                                //pickup Location

                                //Animacao
                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoits = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoits);
                                        blackPolyline.setPoints(p);


                                    }
                                });
                                polylineAnimator.start();
//                            carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
//                                    .flat(true)
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));

                                handler = new Handler();
                                index = -1;
                                next = 1;

                                handler.postDelayed(drawPathRunnable, 300);


                            }
                        } catch (Exception e) {
                            emitter.onError(e);
                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        emitter.equals(t);
                    }
                });

            } catch (Exception e) {
                emitter.onError(e);
            }


        });
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


    public static List decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

// Metodos para Rota
    //TODO: Rotas

    // Fim metodos para rotas

    private void init() {

        mGps.setOnClickListener(v -> {
            Log.d(TAG, "onClick,init: localizacaoActual");

            //showDriversList();
            getDeviceLocation();
//
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Controller.getInstance().getPLACE_PICKER_REQUEST()) {
            Toast.makeText(this, "pickup cod " + Controller.getInstance().getPLACE_PICKER_REQUEST() + "Request Code:" + requestCode, Toast.LENGTH_SHORT).show();

            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place : %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Buscando a localizacao Actual");
        // Toast.makeText(this, "Buscando a localizacao Actual", Toast.LENGTH_SHORT).show();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();


                ((Task) location).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Localizacao encontrada");
                            currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                mLastLocation = currentLocation;
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My location");
                                //getDriverLocation(createPlace(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())),"UH8w15G4NtMWtXQk08KqTzzQxdF2");

                            }

                        } else {
                            Log.d(TAG, "onComplete: Local actual nulo");
                            Toast.makeText(ClientMapActivity.this, "Não foi possível carregar a localização actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Excepção de Segurançaa" + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title) {// mover a camera
        Log.d(TAG, "moveCamera: movendo a camera para latitude:" + latLng.latitude + "longitude:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //Opcoes de marcador
        if (!title.equals("My location")) {

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options); // Adicionando o marcador ao mapa

        }
        // hideSoftKeyboard();
    }

    private void initMap() {// inicializar o mapa
        Log.d(TAG, "initMap: inicializando o mapa");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ClientMapActivity.this);

    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: buscando permissao de localizacao");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permissao falhou");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permissao consedida");
                    // Inicializa o mapa pois tudo esta bem
                    initMap();
                }
            }
        }
    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//medi bang

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        Log.d(TAG, "Estado de connecao: " + mGoogleApiClient.isConnected() + "mLastLocation " + mLastLocation);
        if (mLastLocation != null) {
//            if (location_swhitch.isChecked()) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //Add Marker
                    if (currentMarker != null) {
                        currentMarker.remove();
                        currentMarker = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.car_marker))
                                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                .title("Voce"));

                        //Move Camera to this position
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

//                            // Animacao do carro
//                            rotateMarker(currentMarker, -360, mMap);

                    }
                }
            });
//            } else {
//                Log.d(TAG, "displayLocation: " + "cannot get the location");
//                Toast.makeText(this, "cannot get the location", Toast.LENGTH_SHORT).show();
//
//            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            displayLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            buildGoogleApiClient();// Permite usar API Google
            // mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    Toast.makeText(ClientMapActivity.this, "Voce", Toast.LENGTH_SHORT).show();
                }
            });

            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            // Tutorial Retrofit
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true); // Habilitar Zoom
            mMap.setMyLocationEnabled(true);
            init();
            // displayLocation();
            // startLocationUpdates();

        }


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        return true;
    }

    public void openDrawer(View view) {
        mDrawerLayout.openDrawer(GravityCompat.START, true);
    }


    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        currentMarker.remove();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Timber.d("updating...");
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        // Toast.makeText(this, "actualizando: " + mFusedLocationProviderClient.getLastLocation(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("Connectado Cliente");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        //  displayLocation();
        // startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();


        if (broadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

//        String userId=FirebaseAuth.init().getCurrentUser().getUid();
//        DatabaseReference dbref=FirebaseDatabase.init().getReference("driversAvailable");
//        GeoFire geoFire=new GeoFire(dbref);
//        geoFire.removeLocation(userId);
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(dbref);
            geoFire.removeLocation(userId);

            Timber.d("saindo com sucesso");
        }
    }


    /*public void customeRequest() {
        Timber.d("pesquisando motoristas");
        if (requestBol) {
            //endRide();


        } else {

            try {

//                Controller instance = Controller.getInstance();
//                mapModel.matchRoute(Trip.builder(currentPerson, createPlace(instance.getStart()), createPlace(instance.getDestination())).build())
//                        .observe(this, stringResponse -> {
//                            if (stringResponse.getRequestResult() == SUCCESSFULL) {
//
//                                Toast.makeText(this, "Pedido feito com sucesso.. Aguarde!", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });

//                requestBol = true;
//                Timber.d("customerRequest...");
//                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//                GeoFire geoFire = new GeoFire(ref);
//                geoFire.setLocation(userId, new GeoLocation(Controller.getInstance().getStart().latitude, Controller.getInstance().getStart().longitude)); //partida
//                geoFire.setLocation(userId, new GeoLocation(Controller.getInstance().getDestination().latitude, Controller.getInstance().getDestination().longitude));//destino
//
//                pickupLocation = new LatLng(Controller.getInstance().getStart().latitude, Controller.getInstance().getStart().longitude);

               // getClosestDriver();
            } catch (NullPointerException e) {
                Timber.d(e.getMessage());

                return;
            }

        }
    }*/


    private int radius = 1;
    private int distance = 1;// 1km
    private Boolean driverFound = false;
    private String driverFoundID;
    private static final int LIMIT = 3;

    GeoQuery geoQuery;


/*    private void getClosestDriver() {

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();
        ver = new Vector<>();

        //ver=Controller.init().getOnDrivers();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound) {

                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    driverFound = true;


                    Timber.d("Id DriverLicence" + key);

                    ver.addPassenger(new DriverLicence(key, "Ligeiro", "A1"));
                    Timber.d("Vector :" + ver.toString());

//                        mMap.addMarker(new MarkerOptions().position(new LatLng(-25.944199,32.541101)).title("Motorissta Disponivel:"+key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_carr)));
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("customerRideId", customerId);

                    showNormalNotification(ClientMapActivity.this, "Mbela Mova", "Procurando Localizacao do motorista", R.drawable.logo, "Procurando Localizacao do motorista");


                    // getDriverLocation();

                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Timber.d("dataSnapshot " + dataSnapshot.toString());
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound) {
                                    Timber.d("Motorista Encontrado1");
                                    return;
                                }
                                Timber.d(" Prourando Motorista ");

                                Timber.d("Motorista Encontrado2");
                                driverFound = true;
                                driverFoundID = dataSnapshot.getKey();

                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                HashMap map = new HashMap();
                                map.put("customerRideId", customerId);
                                map.put("destination", Controller.getInstance().getDestinationName());
                                map.put("destinationLat", Controller.getInstance().getDestination().latitude);
                                map.put("destinationLng", Controller.getInstance().getDestination().longitude);
                                driverRef.updateChildren(map);

                                getDriverLocation(driverFoundID);
                                getDriverInfo();
                                getHasRideEnded();


                            }


                            showDriversList();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }*/


    private boolean verifyYourAccountBalance() {

        miniValueToPay = valueToPay(volume, fuelPrice);

        Timber.d("Variavel a pagar" + miniValueToPay);
        Timber.d("Saldo disponivel" + saldoDisponivel);

        if (saldoDisponivel < miniValueToPay) {
            return false;
        } else {
            return true;
        }

    }

    private void showNormalNotification(BaseActivity activity, String mbela_mova, String
            procurando_localizacao_do_motorista, int logo, String procurando_localizacao_do_motorista1) {
    }

    private void showDriversList(Bundle extra) {
        String rides = extra.getString("rides");
        String ride = extra.getString("ride");
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        List<AvailableRide> listOfRides;
        if (extra.containsKey("ride")) {
            AvailableRide singleRide = gson.fromJson(ride, AvailableRide.class);
            listOfRides = Collections.singletonList(singleRide);

        } else if (extra.containsKey("rides")) {
            Type listType = new TypeToken<ArrayList<AvailableRide>>() {
            }.getType();

            listOfRides = gson.fromJson(rides, listType);


        } else listOfRides = new ArrayList<>();
        Timber.d("listOfRides: %s", listOfRides);
        mapModel.setDriversList(listOfRides);

        mapModel.getUser().observe(this, user -> {
            Timber.d("current Person to driver: %s", user);
            DriversListBottomSheet driversListFragment = DriversListBottomSheet.fromPerson(user);
            driversListFragment.show(getSupportFragmentManager(), "DRIVERS_LIST");
        });


    }

    /*-------------------------------------------- Map specific functions -----
        |  Function(s) getDriverLocation
        |
        |  Purpose:  Get's most updated driver location and it's always checking for movements.
        |
        |  Note:
        |	   Even tho we used geofire to push the location of the driver we can use a normal
        |      Listener to get it's location with no problem.
        |
        |      0 -> Latitude
        |      1 -> Longitudde
        |
        *-------------------------------------------------------------------*/
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;

    private void getDriverLocation(mmconsultoria.co.mz.mbelamova.model.Place place, String driverFoundID, String driverName) {
        if (place == null || TextUtils.isEmpty(driverFoundID) || TextUtils.isEmpty(driverName)) {
            if (driverLocationRefListener != null) {
                FirebaseDatabase.getInstance().getReference().removeEventListener(driverLocationRefListener);
            }
        }

        Timber.d("driverId: %s", driverFoundID);
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversAvailable").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String driverStatus = "Offline";

                if (dataSnapshot.exists()) {
                    Timber.i("path: %s \nData location %s \n classType: %s", dataSnapshot.getRef().getKey(), dataSnapshot.getValue(), dataSnapshot.getValue().getClass().getName());


                    driverStatus = "Online";

                    List<Object> map = (List<Object>) dataSnapshot.getValue();

                    Timber.d("mapa: %s ", map);

                    if (map == null) {
                        return;
                    }

                    Timber.d("lat: %s long: %s", map.get(0), map.get(1));

                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }

                    lastDriverLocation = new LatLng(locationLat, locationLng);
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(place.getLatitude());
                    loc1.setLongitude(place.getLongitude());

                    Location loc2 = new Location("");
                    loc2.setLatitude(lastDriverLocation.latitude);
                    loc2.setLongitude(lastDriverLocation.longitude);

                    float distance = loc1.distanceTo(loc2);

                    Timber.d("Distancia: %s", distance);

                    if (hasDriverMetClient)
                        if (distance < 100) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ClientMapActivity.this);
                            builder.setTitle("Motorista chegou").create().show();
                            hasDriverMetClient = true;
                        } else {
                            Timber.d("Esta longe");
                            // tracarRota.setText("DriverLicence Found: " + String.valueOf(distance) + " m");
                        }


                    mDriverMarker = mMap.addMarker(new MarkerOptions()
                            .position(lastDriverLocation)
                            .title(driverName)
                            .snippet(driverStatus)
                            .icon(BitmapDescriptorFactory.
                                    fromResource(R.drawable.car_marker))
                    );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get fromUser the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    private void getDriverInfo() {
        // mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mDriverName.setText(map.get("name").toString());
                    }
                    if (map.get("phone") != null) {
                        mDriverPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("car") != null) {
                        mDriverCar.setText(map.get("car").toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        Picasso.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mDriverProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;

    private void getHasRideEnded() {
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide() {
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
//ToDO ver isso
//        if(pickupMarker != null){
//            pickupMarker.remove();
//        }
        if (mDriverMarker != null) {
            mDriverMarker.remove();
        }
        //tracarRota.setText("call Mova");

//        mDriverInfo.setVisibility(View.GONE);
//        mDriverName.setText("");
//        mDriverPhone.setText("");
//        mDriverCar.setText("Destination: --");
//        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION_CLIENT));

        startLocationUpdates();
        mGoogleApiClient.connect();
        buildGoogleApiClient();
        // updatePersonInfo();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


    private void updatePersonInfo(Person person) {
        this.currentPerson = person;

        Timber.d("Current person: %s", person);

        if (person != null) {
            saldoDisponivel = person.getMoneyAvailable();

/*
            Toast.makeText(this, "Saldo" + saldoDisponivel, Toast.LENGTH_SHORT).show();
*/

            Timber.d("Saldo do updatePerson" + saldoDisponivel);


            if (!TextUtils.isEmpty(person.getPhotoUri()))
                Picasso.with(this)
                        .load(person.getPhotoUri())
                        .placeholder(R.drawable.userphoto)
                        .fit()
                        .into(mPerfilFoto);
            Timber.d("Apelido" + person.getLastName());

            nav_profile_name.setText(person.getName());


            nav_profile_balance.setText(String.format("%.3f", person.getMoneyAvailable()) + " MT");


            /*mapModel.requestCurrentPosition(person.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onGoingRideResponse -> {
                        onUpdateCurrentPositions(onGoingRideResponse.getData());
                    }, mapModel::onError);*/


        }


    }

    public void acceptLocationPermission() {
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(this)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(getString(R.string.location_dialog_msg));
                dialog.setPositiveButton(getString(R.string.location_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toasty.error(this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmTrip(String rideId) {
        confirmTrip = FireFunctions.init().confirmTripClient(rideId);
        confirmTrip.observe(this, this::onConfirmTrip);
    }

    private void onConfirmTrip(mmconsultoria.co.mz.mbelamova.fragment.Response<FireRequestResponse> response) {
        if (response.getRequestResult() == RequestResult.SUCCESSFULL) {
            Toasty.success(this, getString(R.string.trip_confirmed)).show();
        }
    }

    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (currentMarker == null)
            currentMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker)).position(latLng));
        else
            MarkerAnimation.animateMarkerToGB(currentMarker, latLng, new LatLngInterpolator.Spherical());

        // loadAllAvailableDrivers();
    }

    private void showDriverMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (currentMarker == null)
            currentMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).position(latLng));
        else
            MarkerAnimation.animateMarkerToGB(currentMarker, latLng, new LatLngInterpolator.Spherical());


    }

    private void loadAllAvailableDrivers(String key, String driverName) {
        if (TextUtils.isEmpty(key)) {
            Timber.d("empty driverId");
            return;
        }

        // Carregar motoristas na distancia de 3km

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference("driversAvailable").child(key);
        GeoFire gf = new GeoFire(driverLocation);
        GeoQuery geoQuery = null;
        try {
            gf.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), distance).addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    LatLng latLng = new LatLng(location.latitude, location.longitude);
                    if (DriverMarker == null)
                        DriverMarker = mMap.addMarker(new MarkerOptions().title(driverName).snippet("Motorista").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).position(latLng));
                    else
                        MarkerAnimation.animateMarkerToGB(DriverMarker, latLng, new LatLngInterpolator.Spherical());
                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
// Carregas dados dos motoristas
                FirebaseDatabase.getInstance().getReference("Person").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DriverLicence driverLicence = dataSnapshot.getValue(DriverLicence.class);
                        // adicionar drivers no mapa

//                        mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(location.latitude,location.longitude))
//                        .flat(true)
//                                .title("Moto 2").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));

                        LatLng latLng = new LatLng(location.latitude, location.longitude);
                        if (DriverMarker == null)
                            DriverMarker = mMap.addMarker(new MarkerOptions().title(driverName).snippet("Motorista").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).position(latLng));
                        else
                            MarkerAnimation.animateMarkerToGB(DriverMarker, latLng, new LatLngInterpolator.Spherical());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.toException());
                    }
                });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= LIMIT) {
                    distance++;
                    //loadAllAvailableDrivers();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    public double valueToPay(double volume, double fuelPrice) {
        return volume * fuelPrice * tripDistance + (volume * fuelPrice * tripDistance * 0.15);
    }

    private void saveTripToLocalStorage(Trip trip) {
        mapModel.saveTrip(trip);
    }


}

