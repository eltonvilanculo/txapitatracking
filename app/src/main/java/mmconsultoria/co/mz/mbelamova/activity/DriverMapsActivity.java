package mmconsultoria.co.mz.mbelamova.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.util.GeoUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mmconsultoria.co.mz.mbelamova.Common.Common;
import mmconsultoria.co.mz.mbelamova.Common.Util;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.adapter.LatLngInterpolator;
import mmconsultoria.co.mz.mbelamova.adapter.MarkerAnimation;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.fragment.TravelDataDriver;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.Controller;
import mmconsultoria.co.mz.mbelamova.model.DriverLicence;
import mmconsultoria.co.mz.mbelamova.model.IGoogleAPI;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Ride;
import mmconsultoria.co.mz.mbelamova.model.Token;
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
import static mmconsultoria.co.mz.mbelamova.R.id.frame_layout;
import static mmconsultoria.co.mz.mbelamova.service.FirebaseNotificationService.NOTIFICATION_BUNDLE;


public class DriverMapsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    private static final String TAG = "DriverMapsActivity";

    //Permissoes
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //Constantes
    private static final float DEFAULT_ZOOM = 15f;
    private static final int RC_SIGN_IN = 9001;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String CHANNEL_ID = "AR1";
    private static int PLACE_PICKER_REQUEST = 1;

    //Widget Vars
    private ImageView mGps;
    private DrawerLayout mDrawerLayout;
    private CircleImageView mPerfilFoto;
    private ImageView navigation_menu;
    private CardView mSettings;
    private TextView nav_profile_name;
    private TextView nav_profile_balance;

    private EditText valorRegarga;
    private TextView txtSaldo;
    private Button btnRecarregar, startRide;
    private AutoCompleteTextView pesquisa_Fiald;
    private FloatingActionButton button_viagem_marcada;
    private FloatingActionButton button_viagem_em_curso;


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
    private String customerId = "";
    private RideStatus rideStatus = RideStatus.NONE;


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

    public Double tripDistance;
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
    private LocationCallback mLocationCallback;
    private DrawerLayout drawer;
    private Person currentPerson;
    private double saldoDisponivel;

    private MapViewModel mapModel;
    private DriverLicence currentDriverLicence;
    private BroadcastReceiver broadcastReceiver;
    private LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response> confirmTripLiveData;
    private LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response<Ride>> lastRide;
    private MutableLiveData<mmconsultoria.co.mz.mbelamova.fragment.Response<Ride>> ride;
    private Ride currentRide;
    private boolean isRideHappening = false;
    private Button driverArrived;
    private boolean isTripScheduled = false;
    boolean hasDriverMetClient = false;
    private Trip currentTrip;
    private LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response> updateTrip;
    private MutableLiveData<mmconsultoria.co.mz.mbelamova.fragment.Response<String>> finishRide;
    private String currentRideId;
    private ImageView dragLocationButton;
    GeoFire geoFireAvailable = null;
    private com.google.android.material.floatingactionbutton.FloatingActionButton button_localizacao_actual;


    private void buildTripListDialog(View v, String rideId) {
        if (rideId == null || rideId.isEmpty()) {
            Toasty.info(this, "Nao ha viagem no momento").show();
            return;
        }


        mapModel.getSingleRide(rideId).observe(this, this::onLoadRide);

    }

    private void onLoadRide(mmconsultoria.co.mz.mbelamova.fragment.Response<Ride> rideResponse) {
        if (rideResponse.getRequestResult() != RequestResult.SUCCESSFULL) {
            Toasty.error(this, "Could not get Ride").show();
            return;
        }

        if (rideResponse.getData() == null) {
            Toasty.error(this, "Viagem nao existe").show();
            return;
        }

        Ride ride = rideResponse.getData();
        ride.setId(rideResponse.getKey());

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverMapsActivity.this);
        builder.setTitle("Clientes na viagem");
        ListView modeList = new ListView(DriverMapsActivity.this);

        ArrayList<Trip> populate = new ArrayList<>(ride.getTrips().values());

        ArrayAdapter<Trip> modeAdapter = new ArrayAdapter<Trip>(DriverMapsActivity.this, R.layout.trip_item, populate) {
            @NonNull
            @Override
            public View getView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(DriverMapsActivity.this).inflate(R.layout.trip_item, parent, false);

                TextView nome = view.findViewById(R.id.trip_item_client_name);
                TextView distance = view.findViewById(R.id.trip_item_distance);
                TextView finish = view.findViewById(R.id.trip_item_button_finish);
                TextView start = view.findViewById(R.id.trip_item_button_start);


                Trip trip = populate.get(position);
                trip.setRideId(ride.getId());

                nome.setText(trip.getClientName());
                distance.setText(GeoUtils.distance(currentLocation.getLatitude(), currentLocation.getLongitude(), trip
                        .getEndPoint()
                        .getLatitude(), trip.endPoint()
                        .getLongitude()) + " km");

                start.setOnClickListener(v -> startCurrentTrip(trip));
                finish.setOnClickListener(v -> finishCurrentTrip(trip));

                return view;
            }
        };

        modeList.setAdapter(modeAdapter);
        builder.setView(modeList);
        final Dialog dialog = builder.create();

        modeAdapter.notifyDataSetChanged();

        dialog.show();


    }

    private void startCurrentTrip(Trip trip) {
        mapModel.startTrip(trip).observe(this, this::onFinishResponse);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        Timber.d("extras: %s", intent.getExtras());
        Timber.d("action: %s", intent.getAction());
        Timber.d("action: %s", intent.getBundleExtra(FirebaseNotificationService.NOTIFICATION_BUNDLE));
        Timber.d("action: %s", intent.getBundleExtra(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION));
        Timber.d("action: %s", intent.getBundleExtra(FirebaseNotificationService.ACTION_ACCEPT));
        Timber.d("action: %s", intent.getBundleExtra(FirebaseNotificationService.EXTRA_ACCEPT));
        Timber.d("action: %s", intent.getBundleExtra(FirebaseNotificationService.ACTION_REJECT));
        Timber.d("action: %s", intent.getBundleExtra(FirebaseNotificationService.EXTRA_REJECT));


        Bundle bundleExtra = intent.getBundleExtra(NOTIFICATION_BUNDLE);
        if (bundleExtra != null) {
            Timber.d("extras: %s", bundleExtra.keySet());

            createdDialog(bundleExtra);
        }

    }

    private void finishCurrentTrip(Trip trip) {
        mapModel.finishTrip(trip).observe(this, this::onFinishResponse);
    }

    private void onFinishResponse(mmconsultoria.co.mz.mbelamova.fragment.Response<String> stringResponse) {
        if (stringResponse.getRequestResult() != RequestResult.SUCCESSFULL) {
            Toasty.error(this, "Nao foi possivel terminar a viagem").show();
        }
    }

    private void cancelCurrentTrip(Trip trip) {
        mapModel.cancelTripDriver(trip).observe(this, this::onFinishResponse);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);


        getLocationPermission();
        // Abilitar Localizacao (GPS)
        acceptLocationPermission();
        buildGoogleApiClient();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Navedation Drawer
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nav_profile_name = headerView.findViewById(R.id.nome_textView);
        nav_profile_balance = headerView.findViewById(R.id.maps_header_balance);
        mPerfilFoto = headerView.findViewById(R.id.perfil_foto);
        navigationView = findViewById(R.id.nav_view);
        navigation_menu = (ImageView) findViewById(R.id.navigation_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout_map);

        pesquisa_Fiald = (AutoCompleteTextView) findViewById(R.id.driver_input_search);
        pesquisa_Fiald.setEnabled(false);
                dragLocationButton = findViewById(R.id.drag_location_marker);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        tracarRota = findViewById(R.id.btn_get_direction);
        partida = findViewById(R.id.btnPartida);
        destino = findViewById(R.id.btnDestino);
        startRide = findViewById(R.id.btn_start_ride);
        driverArrived = findViewById(R.id.btn_driver_arrived);
        driverArrived.setVisibility(View.GONE);

        distanciaViagem = (TextView) findViewById(R.id.distanciaTV_moto);
        duracaoViagem = (TextView) findViewById(R.id.duracaoTV_moto);
        detalhesViagemCardView = (CardView) findViewById(R.id.detalhesViagem_moto);

        //pagamento
        View customView = getLayoutInflater().inflate(R.layout.recharge_dialog, null, false);
        valorRegarga = customView.findViewById(R.id.valor_a_recarregar);
        btnRecarregar = customView.findViewById(R.id.recharge_button);


        button_localizacao_actual = findViewById(R.id.button_localizacao_actual);
        button_viagem_marcada = findViewById(R.id.button_viagem_marcada);
        button_viagem_em_curso = findViewById(R.id.button_viagem_em_curso);


        button_viagem_marcada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (currentRide != null)
            button_viagem_em_curso.setOnClickListener(v1 -> buildTripListDialog(v1, currentRide.getId()));


        button_localizacao_actual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();

            }
        });


        startRide.setOnClickListener(v -> {
            if (rideStatus == RideStatus.STARTED) {
                endRide();
            }

            if (rideStatus == RideStatus.SCHEDULED) {
                startRide();
            }
        });

        driverArrived.setOnClickListener(v -> {
            if (rideStatus == RideStatus.SCHEDULED) {

                if (currentTrip != null) {
                    currentTrip.setStatus(TripStatus.DRIVER_ARRIVED);
                    currentTrip.setChangedBy("DRIVER");

                    if (currentRide != null) {
                        updateTrip = mapModel.updateTrip(currentTrip, currentRide.getId());
                        updateTrip.observe(this, observe -> {
                            if (observe.getRequestResult() != RequestResult.SUCCESSFULL) {
                                Toasty.success(this, "Nao foi possivel informar o cliente").show();
                            }

                        });
                    }
                }

            }

        });

        pesquisa_Fiald.setOnClickListener(v -> {
            if (currentPerson.getDriverData() == null) {
                Toasty.info(this, "Sem dados de motorista").show();
                return;
            }

            TravelDataDriver travelDataDriver = TravelDataDriver.fromPerson(currentPerson);

            swapFragmentAndAddToBackStack(frame_layout, travelDataDriver, null, null);

//                DriversListBottomSheet myFragment=new DriversListBottomSheet();
//                myFragment.show(getSupportFragmentManager(),"Exemplo de Bottom Sheet");
        });

        final Controller instance = Controller.getInstance();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {


                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    String userId = null;

                    if (currentUser != null) {
                        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                        userId = currentUser.getUid();
                        geoFireAvailable = new GeoFire(refAvailable);

                        geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));

                        CirculationAreaControl(geoFireAvailable);
                    }

                    // checkPoint();


                    mLastLocation = location;

                    if (locationResult.getLastLocation() == null)
                        return;
                    currentLocation = locationResult.getLastLocation();
//                    if (firstTimeFlag && mMap != null) {
////                        animateCamera(currentLocation);
////                        firstTimeFlag = false;
////                    }
//                    if(Controller.getInstance().getDestination()!=null){
//                        LatLng latLng=.getLatitude(),currentLocation.getLongitude;
//                        if(=Controller.getInstance().getDestination()){
//                            Toast.makeText(DriverMapsActivity.this, "Chegou ao Destino", Toast.LENGTH_SHORT).show();
//                        }
//                    }

                    try {
                        if (instance.getDestination() != null) {
                            if ((instance.getDestination().latitude) == currentLocation.getLatitude()) {
                                Toast.makeText(DriverMapsActivity.this, "Chegou Ao Destino", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showMarker(location);


                    // if (currentMarker != null) {

//                    currentMarker = mMap.addMarker(new MarkerOptions()
//                            .icon(BitmapDescriptorFactory
//                                    .fromResource(R.drawable.car_marker))
//                            .position(posiMto)
//                            .title("Motorista:"+userId));

                    if (getApplicationContext() != null) {

                        mLastLocation = location;
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


                        DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                        GeoFire geoFireWorking = new GeoFire(refWorking);

//                        switch (customerId) {
//                            case "":
//                                if (geoFireWorking != null) {
//                                    geoFireWorking.removeLocation(userId);
//                                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//                                }
//                                break;
//
//                            default:
//                                geoFireAvailable.removeLocation(userId);
//                                Timber.d("driversWorking addPassenger");
//                                geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//                                break;
//                        }
                    }


                    // mMap.clear();

                    //Move Camera to this position
                    //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posiMto, 15.0f));
                    //currentMarker.remove();
//                            // Animacao do carro
//                            rotateMarker(currentMarker, -360, mMap);

                    //   }

//                    mMap.addMarker(new MarkerOptions()
//                            .position(posiMto).flat(true)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker))
//                            .title("Motorista:"+userId));

                }

            }

        };

        // Nevegation Drawer
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        // set item as selected to persist highlight
//                        menuItem.setChecked(false);
//                        // close drawer when item is tapped
//                        mDrawerLayout.closeDrawers();
//
//                        // Add code here to update the UI based on the item selected
//                        // For example, swap UI fragments here
//                        int id = menuItem.getItemId();
//
//
//                        if (id == R.id.nav_pedir_boleia) {
//                            Intent homeIntent = new Intent(DriverMapsActivity.this, ClientMapActivity.class);
//                            startActivity(homeIntent);
//                            finish();
//
//
//                        } else if (id == R.id.nav_definicoes) {
//                            startMyActivity(SettingsActivity.class);
//                        } else if (id == R.id.nav_promocao) {
//                            startMyActivity(PromoActivity.class);
//                        } else if (id == R.id.nav_historico) {
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString(DatabaseValue.USER_ID.name(), currentPerson.getId());
//                            startActivity(StatsActivity.class, bundle, null);
//
//                        }
//
//                        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
//                        drawer.closeDrawer(GravityCompat.START);
//                        return true;
//                    }
//                });

        tracarRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Controller instance = Controller.getInstance();
                mmconsultoria.co.mz.mbelamova.model.Place place = createPlace(instance.getStart());

                if (internetVerification()) {
                    getDirection(place, instance.getDestinationName())
                            .subscribeOn(Schedulers.io())
                            .subscribe(success -> {

                                Ride build = Ride.builder(currentPerson, "", instance.getNumberOfAvailableSeats(), System.currentTimeMillis())
                                        .build();
                                mapModel.createRide(build)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(onSuccess -> {
                                            subscribeToNotification(onSuccess.getKey());
                                            Toast.makeText(DriverMapsActivity.this, getString(R.string.trip_scheduled), Toast.LENGTH_SHORT).show();

                                            build.setId(onSuccess.getKey());
                                            mapModel.saveRide(build);


                                        }, mapModel::onError);
                            }, throwable -> {
                                Timber.e(throwable, "could not get direction");
                            });

//                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DriverMapsActivity.this);
//                    //alertBuilder.setTitle("Recarga");
//                    alertBuilder.setView(customView);
//                    AlertDialog alertDialog = alertBuilder.create();
//                    valorRegarga.setText(String.format("%.3f",
//                            valueToPay(volume, fuelPrice)
//                            )
//                    );
//                    alertDialog.show();
//
//                    btnRecarregar.setOnClickListener(v -> {
//                        alertDialog.hide();
//
//                        payMpesa();
//                        alertDialog.dismiss();
//                        alertDialog.cancel();
//                    });

                    customeRequest();
                }


            }


            private boolean internetVerification() {
                boolean aux = false;
                if (Util.Operations.isOnline(DriverMapsActivity.this)) {
                    aux = true;
                } else {
                    aux = false;
                    snackBar(findViewById(R.id.drawer_layout_client), "Sem Conexão a Internet");
                }
                return aux;
            }
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

        updateFirebaseToken();

        lastRide = mapModel.getLastRide();
        lastRide.observe(this, this::processRide);

        readNotifications();
        registerBroadCast();

        onNewIntent(getIntent());
    }//

    private void CirculationAreaControl(GeoFire geoFireAvailable) {
        {  LatLng circulationArea=new LatLng(-25.955852,32.581272);

            GeoQuery geoQuery=geoFireAvailable.queryAtLocation(new GeoLocation(circulationArea.latitude,circulationArea.longitude),2f);

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.d(TAG, "Entrou Area de Circulacao: ");

                    Toast.makeText(DriverMapsActivity.this, "Dentro da Area Valida", Toast.LENGTH_SHORT).show();
                   // SendNotification("TXAPITA MOBILITY",String.format("% Entrou na Area de Circulacao"));
                }

                @Override
                public void onKeyExited(String key) {
                  //  SendNotification("TXAPITA MOBILITY",String.format("% Saio na Area de Circulacao"));
                    pesquisa_Fiald.setText("Saio Area de Circulacao: ");
                    Log.d(TAG, "Saio da Area de Circulacao: ");
                    Toast.makeText(DriverMapsActivity.this, "Fora da Area Valida", Toast.LENGTH_SHORT).show();
                    SendNotification("SAio da Area de Circulacao","Por favor retorne a Area de Circulacao");
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.d(TAG, "onKeyMoved: "+"Em Movimenteo");

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Log.d(TAG, "ERROR: "+error);
                }
            });
        }
    }

    private void processRide(mmconsultoria.co.mz.mbelamova.fragment.Response<Ride> rideResponse) {
        if (rideResponse.getRequestResult() == RequestResult.SUCCESSFULL) {
            currentRide = rideResponse.getData();
        }
    }


    private void registerBroadCast() {

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION_DRIVER)) {
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
        filter.addAction(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION_DRIVER);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    private void confirmDriverArrived() {

    }

    private void checkPoint(String destination) {


    }

    private void readNotifications() {

        setOnTripUpdatedListener(this::onNotificationTripUpdated);
/*
        Bundle bundle = getIntent().getBundleExtra(FirebaseNotificationService.NOTIFICATION_BUNDLE);


        if (bundle == null) {
            Timber.i("No new notification");
            return;
        }
*//*

        String sender = bundle.getString("sender");

        Timber.i("notification received: %s", sender);*/

/*
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION)) {
                    Bundle bundle = intent.getBundleExtra(FirebaseNotificationService.NOTIFICATION_BUNDLE);
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

                    //startActivity(NotificationActivity.class, bundle, null);
                    String fromUser = bundle.getString("senderName");
                    String messageType = bundle.getString("messageType");
                    String rideId = bundle.getString("rideId");
                    String tripId = bundle.getString("tripId");
                    //startActivity(NotificationActivity.class, bundle, null);

                    mmconsultoria.co.mz.mbelamova.fragment.ProgressDialog dialog = new mmconsultoria.co.mz.mbelamova.fragment.ProgressDialog(DriverMapsActivity.this);
                    dialog.setText("Aguarde...");
                    dialog.show();

                    LiveData<mmconsultoria.co.mz.mbelamova.fragment.Response<Trip>> tripsLiveData = mapModel.getSingleTrip(rideId, tripId);
                    tripsLiveData.observe(DriverMapsActivity.this, response -> {
                        switch (response.getRequestResult()) {
                            case SUCCESSFULL:
                                Trip trip = response.getData();
                                trip.setId(response.getKey());
                                dialog.dismiss();
                                createDialog(trip, rideId, sender);
                                break;
                            default:
                                Toasty.error(DriverMapsActivity.this, "Could not get Trip").show();
                                dialog.dismiss();
                        }
                    });


                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION));

*/

    }

    private void onNotificationTripUpdated(String rideId, Trip trip) {
        Timber.d("trip: %s ", trip);


        currentTrip = trip;
        currentRideId = rideId;

        observeRide(rideId);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Pedido de Viagem")
                .setMessage("De: " + trip.name());

        if (TripStatus.CLIENT_REQUESTED == trip.getStatus()) {
            builder.setNegativeButton("Recusar", (dialog, which) -> {
                rejectTripDriver(rideId, trip);
            }).setPositiveButton("Aceitar", (dialog, which) -> {

                confirmTripDriver(rideId, trip);

            }).setCancelable(false).show();

        }

        if (TripStatus.FINISHED == trip.getStatus()) {
            currentRideId = null;
            builder.setTitle("Viagem Terminou")
                    .setPositiveButton("OK", (dialog, which) -> {
                    }).setCancelable(true).show();
        }

        if (TripStatus.CLIENT_CANCELED == trip.getStatus()) {
            builder.setTitle("Viagem Cancelada")
                    .setPositiveButton("OK", (dialog, which) -> {
                    }).setCancelable(false).show();
        }

        if (TripStatus.SCHEDULED == trip.getStatus()) {
            builder.setTitle("Viagem Agendada").setPositiveButton("OK", (dialog, which) -> {
                observeRide(rideId);
            }).setCancelable(false).show();

            driverArrived.setVisibility(View.VISIBLE);

            rideStatus = RideStatus.SCHEDULED;

            isTripScheduled = true;
            showClientPosition(trip);
            getDirection(trip.getStartPoint(), trip.endPoint().getName())
                    .subscribeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(next -> {

                        Timber.d("tracar routa = " + next);

                    }, throwable -> {
                        Toasty.error(this, "Nao foi possivel trcar a routa").show();
                    });
        } else driverArrived.setVisibility(View.GONE);

    }


    private void showClientPosition(Trip trip) {
        MarkerOptions startPoint = new MarkerOptions();
        startPoint.title("Partida").position(new LatLng(trip.startPoint().getLatitude(), trip.startPoint().getLongitude()));

       /* MarkerOptions endPoint = new MarkerOptions();
        endPoint.title("Destino").position(new LatLng(trip.getEndPoint().getLatitude(), trip.endPoint().getLatitude()));*/

        mMap.addMarker(startPoint);
        //mMap.addMarker(endPoint);
    }

    private void removeClientPosition(Trip trip) {
        MarkerOptions options = new MarkerOptions();
        options.title("partida").position(new LatLng(trip.startPoint().getLatitude(), trip.startPoint().getLongitude()));
        mMap.clear();
    }

    private void observeRide(String rideId) {
        if (TextUtils.isEmpty(rideId)) {
            Timber.w("empty rideId");
            return;
        }

        ride = mapModel.getSingleRide(rideId);
        mmconsultoria.co.mz.mbelamova.fragment.ProgressDialog rideDialog = new mmconsultoria.co.mz.mbelamova.fragment.ProgressDialog(this);
        rideDialog.setText("Aguarde...");
        rideDialog.show();


        ride.observe(this, response -> {
            Timber.i("Ride: %s", response.getData());

            if (response.getRequestResult() == RequestResult.SUCCESSFULL) {
                currentRide = response.getData();
                currentRide.setId(response.getKey());
                //startRide.setVisibility(View.VISIBLE);
                rideDialog.dismiss();
            }
        });
    }


    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());

        try {
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void startRide() {
        if (currentRide == null) {
            Timber.i("current ride not availiable");
            return;
        }

        if (currentRide.getTrips() == null || currentRide.getTrips().isEmpty()) {
            Toasty.error(this, "nao ha viagens agendadas").show();
            return;
        }

        mapModel.startRide(currentRide).observe(this, observe -> {
            if (observe.getRequestResult() == RequestResult.SUCCESSFULL) {
                Toasty.success(this, "Viagem iniciada").show();
                startRide.setText("Terminar Viagem");
                isRideHappening = true;
                rideStatus = RideStatus.STARTED;
            } else {
                Toasty.success(this, "Nao foi possivel inicair a viagem").show();
            }
        });
    }


    private void snackBar(View viewById, String s) {
        Snackbar.make(viewById, s, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "LocationChanging...", Toast.LENGTH_SHORT).show();
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f));
//        String userId=FirebaseAuth.init().getCurrentUser().getUid();
//        DatabaseReference dbref=FirebaseDatabase.init().getReference("driversAvailable");
//        GeoFire geoFire=new GeoFire(dbref);
//       geoFire.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));

    }

    public void cleanDestinoFild(View view) {
        TextView destinoFild = findViewById(R.id.input_search);
        destinoFild.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapModel.getLastRide().observe(this, this::onLoadRideFromLocal);
    }

    private void onLoadRideFromLocal(mmconsultoria.co.mz.mbelamova.fragment.Response<Ride> rideResponse) {
        if (rideResponse.getRequestResult() == RequestResult.SUCCESSFULL) {
            currentRide = rideResponse.getData();
        }
    }

    public DriverLicence getCurrentDriverLicence() {
        return currentDriverLicence;
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

    private mmconsultoria.co.mz.mbelamova.model.Place createPlace(LatLng latLng) {
        return mmconsultoria.co.mz.mbelamova.model.Place.build(latLng.latitude, latLng.longitude).build();
    }


    private LatLng toLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

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


    public Single<Ride> getDirection(mmconsultoria.co.mz.mbelamova.model.Place place, String destination) {
        return Single.create(emitter -> {

            currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            String requestApi = null;

//                    carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
//                    .flat(true)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));
//           CarMoveAnim.startcarAnimation(carMarker,mMap, Controller.getInstance().getStart(),Controller.getInstance().getDestination(),0,null);

            try {
                final Controller controller = Controller.getInstance();
                LatLng start = new LatLng(place.getLatitude(), place.getLongitude());


                Timber.i("routa:=> ,latitude: %s, longitude: %s, destination: %s", start.latitude, start.latitude, destination);

                runOnUiThread(() -> pesquisa_Fiald.setText(destination));

                Timber.d("Destination: " + destination + "Start: " + start);
                requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "mode=driving&" +
                        "transit_routing_preference=less_driving&" +
                        "origin=" + start.latitude + "," + start.longitude + "&" +
                        "destination=" + destination + "&" +
                        "key=" + getResources().getString(R.string.google_direction_api);
                Timber.d("getDirection: " + requestApi);// Print Url for Debug
                mService.getPath(requestApi).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            //
                            //JSONObject rout = jsonArray.getJSONObject(0);
                            JSONArray legs = new JSONArray(jsonArray.getJSONObject(0).getString("legs"));
                            JSONObject legs_info = legs.getJSONObject(0);
                            JSONArray steps = legs_info.getJSONArray("steps");
                            duracao = legs_info.getJSONObject("duration").getString("text");
                            String distancia = legs_info.getJSONObject("distance").getString("text");
                            tripDistance = parseDouble(distancia.replace("km", "").replaceAll(" ", "").replace(",", ""));

                            Timber.d("duracao: " + duracao);
                            Timber.d("distancia: " + distancia);
                            distanciaViagem.setText(distancia + "");
                            duracaoViagem.setText(duracao + "");


//                        tripDistance = Double.parseDouble(legs_info.getJSONObject("distance").getString("text"));

//                      //ProgresDialog
//                        progressDialog = ProgressDialog.show(DriverMapsActivity.this, "Por favor Aguarde.",
//                                "Processando a Rota.", true,true);
//                        requestApi.build();
//                        routing.execute();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                polyLineList = decodePoly(polyline);

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

                                //mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Ponto de Busca").icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));

                                runOnUiThread(() -> tracarRota.setText("Buscando Motoristas..."));

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1))
                                        .title("Destino: " + controller.getDestinationName() + " Duracao: " + duracao + " Distancia: " + distancia).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
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
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);

                                    }
                                });
                                polylineAnimator.start();
                                carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));

                                handler = new Handler();
                                index = -1;
                                next = 1;

                                handler.postDelayed(drawPathRunnable, 300);
                                Timber.d("current person: %s", currentPerson);
                                Ride ride = Ride.builder(currentPerson, polyline, controller.getNumberOfAvailableSeats(), System.currentTimeMillis()).build();
                                emitter.onSuccess(ride);
                            }

                        } catch (Exception e) {
                            Timber.d(e);
                            emitter.onError(e);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Timber.d(t);
                        emitter.onError(t);
                    }
                });

            } catch (Exception e) {
                emitter.onError(e);
            }
        });

    }

    public Single<Ride> getDirection(mmconsultoria.co.mz.mbelamova.model.Place place, mmconsultoria.co.mz.mbelamova.model.Place destination) {
        return Single.create(emitter -> {

            currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            String requestApi = null;

//                    carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
//                    .flat(true)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));
//           CarMoveAnim.startcarAnimation(carMarker,mMap, Controller.getInstance().getStart(),Controller.getInstance().getDestination(),0,null);

            try {
                final Controller controller = Controller.getInstance();
                LatLng start = new LatLng(place.getLatitude(), place.getLongitude());


                Timber.i("routa:=> ,latitude: %s, longitude: %s, destination: %s", start.latitude, start.latitude, destination);

                runOnUiThread(() -> pesquisa_Fiald.setText(destination.getName()));

                Log.d(TAG, "Destination: " + destination + "Start: " + start);
                requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "mode=driving&" +
                        "transit_routing_preference=less_driving&" +
                        "origin=" + start.latitude + "," + start.longitude + "&" +
                        "destination=" + destination.getLatitude() + "," + destination.getLongitude() + "&" +
                        "key=" + getResources().getString(R.string.google_direction_api);
                Log.d(TAG, "getDirection: " + requestApi);// Print Url for Debug
                mService.getPath(requestApi).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            //
                            //JSONObject rout = jsonArray.getJSONObject(0);
                            JSONArray legs = new JSONArray(jsonArray.getJSONObject(0).getString("legs"));
                            JSONObject legs_info = legs.getJSONObject(0);
                            JSONArray steps = legs_info.getJSONArray("steps");
                            duracao = legs_info.getJSONObject("duration").getString("text");
                            String distancia = legs_info.getJSONObject("distance").getString("text");
                            tripDistance = parseDouble(distancia.replace("km", "").replaceAll(" ", "").replace(",", ""));

                            Log.d(TAG, "duracao: " + duracao);
                            Log.d(TAG, "distancia: " + distancia);
                            distanciaViagem.setText(distancia + "");
                            duracaoViagem.setText(duracao + "");


//                        tripDistance = Double.parseDouble(legs_info.getJSONObject("distance").getString("text"));

//                      //ProgresDialog
//                        progressDialog = ProgressDialog.show(DriverMapsActivity.this, "Por favor Aguarde.",
//                                "Processando a Rota.", true,true);
//                        requestApi.build();
//                        routing.execute();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                polyLineList = decodePoly(polyline);

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

                                //mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Ponto de Busca").icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
                                tracarRota.setText("Buscando Motoristas...");

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1))
                                        .title("Destino: " + controller.getDestinationName() + " Duracao: " + duracao + " Distancia: " + distancia).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
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
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);

                                    }
                                });
                                polylineAnimator.start();
                                carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));

                                handler = new Handler();
                                index = -1;
                                next = 1;

                                handler.postDelayed(drawPathRunnable, 300);
                                Timber.d("current person: %s", currentPerson);
                                Ride ride = Ride.builder(currentPerson, polyline, controller.getNumberOfAvailableSeats(), System.currentTimeMillis()).build();
                                emitter.onSuccess(ride);
                            }

                        } catch (Exception e) {
                            Timber.d(e);
                            emitter.onError(e);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Timber.d(t);
                        emitter.onError(t);
                    }
                });

            } catch (Exception e) {
                emitter.onError(e);
            }
        });

    }


    private List<LatLng> decodePoly(String encoded) {

        List poly = new ArrayList();
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
//            Intent intent=new Intent(this, CustommerCall.class);
//            intent.putExtra("lat",Controller.getInstance().getCurrentLocation().latitude);
//            intent.putExtra("long",Controller.getInstance().getCurrentLocation().longitude);
//            startActivity(intent);

            getDeviceLocation();
//
        });

    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: Localizacao Geografica");


        Geocoder geocoder = new Geocoder(DriverMapsActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(destination, 1);

        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOException" + e.getMessage());

        }

        if (list.size() > 0) {
            Address address = list.get(0); // Localizacao pesquisada
            Log.d(TAG, "geoLocate: Localizacao Localizada " + address.toString());
            //  Toast.makeText(this, "Localizacao Localizada "+address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, "Dentro", Toast.LENGTH_LONG).show();
                // Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
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


                ((Task) location).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Localizacao encontrada");
                        currentLocation = (Location) task.getResult();
                        if (currentLocation != null) {
                            mLastLocation = currentLocation;
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My location");
                        }

                    } else {
                        Log.d(TAG, "onComplete: Local actual nulo");
                        Toast.makeText(DriverMapsActivity.this, "Não foi possível carregar a localização actual", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Excepção de Segurançaa" + e.getMessage());
        }
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
        Timber.d("is empty current focus: %s", TextUtils.isEmpty(currentFocus));


        if (TextUtils.isEmpty(currentFocus)) {
            instance.setStart(null);
            instance.setDestinationName(null);
        } else {
            mMap.addMarker(options);
        }

        Timber.d("Button Visibility: %s", dragLocationButton.getVisibility());

        if (currentFocus.equals(Controller.FOCUS_START))
            instance.setStart(target);
        if (currentFocus.equals(Controller.FOCUS_END))
            instance.setDestination(target);


    }

    @Override
    public void onCameraMove() {

        // display imageView
        Controller instance = Controller.getInstance();
        String currentFocus = instance.getCurrentFocus();

        if (TextUtils.isEmpty(currentFocus)) {
            dragLocationButton.setVisibility(View.INVISIBLE);
            return;
        } else {
            mMap.clear();
            dragLocationButton.setVisibility(View.VISIBLE);
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
        mapFragment.getMapAsync(DriverMapsActivity.this);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

            geoFire.setLocation(getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
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

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Toast.makeText(this, "Mapa inicializado com Sucesso!", Toast.LENGTH_LONG).show();

        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            displayLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            buildGoogleApiClient();// Permite usar API Google
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    Toast.makeText(DriverMapsActivity.this, "Voce", Toast.LENGTH_SHORT).show();
                }
            });

            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            // Tutorial Retrofit
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Instantiates a new CircleOptions object and defines the center and radius
            LatLng circulationArea=new LatLng(-25.955852,32.581272);
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(circulationArea.latitude,circulationArea.longitude))
                    .radius(2000)// In meters
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(2f)
                    ;

            // Get back the mutable Circle
            Circle circle = mMap.addCircle(circleOptions);
            // 0.5f 0.5km 500m
//
            init();
            // displayLocation();
            // startLocationUpdates();

        }
    }

    private void SendNotification(String title, String content) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.add_marker)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
       // NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

//        Intent intent =new Intent(this,DriverMapsActivity.class);
//        PendingIntent pendingIntent =PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);
//        builder.setContentIntent(pendingIntent);
////        Notification notification=builder.build();
////        notificationManager.notify(1, b.build());
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

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
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

        if (mFusedLocationProviderClient != null) {
//            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
//
//            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("driversAvailable");
//            GeoFire geoFire = new GeoFire(dbref);
//            geoFire.removeLocation(userId);

            Timber.d("saindo com sucesso");
        }
    }

    public void customeRequest() {
        if (requestBol) {
            endRide();


        } else {
//            int selectId = mRadioGroup.getCheckedRadioButtonId();
//
//            final RadioButton radioButton = (RadioButton) findViewById(selectId);
//
//            if (radioButton.getText() == null) {
//                return;
//            }

            //requestService = radioButton.getText().toString();

            requestBol = true;

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(ref);

            geoFire.setLocation(userId, new GeoLocation(Controller.getInstance().getStart().latitude, Controller.getInstance().getStart().longitude));
            pickupLocation = new LatLng(Controller.getInstance().getStart().latitude, Controller.getInstance().getStart().longitude);

            getClosestDriver();
        }
    }


    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;

    private void getClosestDriver() {

        {
            DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

            GeoFire geoFire = new GeoFire(driverLocation);
            geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
            geoQuery.removeAllListeners();

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (!driverFound && requestBol) {
                        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                    Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                    if (driverFound) {
                                        return;
                                    }

                                    if (driverMap.get("service").equals(requestService)) {
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

                                        getDriverLocation();
                                        getDriverInfo();
                                        getHasRideEnded();
                                        tracarRota.setText("Looking for DriverLicence Location....");
                                    }
                                }
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
        }
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

    private void getDriverLocation() {
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLng);
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (!hasDriverMetClient) {
                        if (distance < 100) {
                            tracarRota.setText("O motorista chegou!");
                        } else {
                            tracarRota.setText("DriverLicence Found: " + String.valueOf(distance) + " m");
                        }
                    }


                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
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
                Timber.e(databaseError.toException());
            }
        });
    }

    enum RideStatus {
        STARTED, ENDED, SCHEDULED, NONE
    }


    private void endRide() {

        try {
            /*geoQuery.removeAllListeners();
            driverLocationRef.removeEventListener(driverLocationRefListener);
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);*/


            finishRide = mapModel.finishRide(currentRide, currentPerson.getMoneyAvailable());
            finishRide.observe(this, observe -> {
                if (observe.getRequestResult() == RequestResult.SUCCESSFULL) {
                    Toasty.success(DriverMapsActivity.this, "Viagem concluida").show();
                    mMap.clear();
                    rideStatus = RideStatus.ENDED;
                    startRide.setVisibility(View.GONE);
                } else {
                    Toasty.error(DriverMapsActivity.this, "Erro ao terminar viagem").show();
                }
            });


        } catch (Exception e) {
            Toasty.error(DriverMapsActivity.this, "Erro ao terminar viagem").show();
            Timber.e(e);
        }


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

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(FirebaseNotificationService.TRIP_NOTIFICATION_ACTION));

        startLocationUpdates();
        mGoogleApiClient.connect();
        // UpdatePersonInfo();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void updatePersonInfo(Person person) {
        this.currentPerson = person;


        if (person != null) {

            saldoDisponivel = person.getMoneyAvailable();
            //Toast.makeText(this, "Saldo" + saldoDisponivel, Toast.LENGTH_SHORT).show();

            Timber.d("Saldo do updatePerson" + saldoDisponivel);


            if (person.getPhotoUri() != null && !person.getPhotoUri().isEmpty())
                Picasso.with(this)
                        .load(person.getPhotoUri())
                        .placeholder(R.drawable.userphoto)
                        .fit()
                        .into(mPerfilFoto);
            nav_profile_name.setText(person.getName());

            nav_profile_balance.setText(String.format("%.3f", person.getMoneyAvailable()) + " MT");

/*

            mapModel.requestCurrentPosition(person.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onGoingRideResponse -> {
                        // TODO: 4/8/2019     onUpdatePositionSuccess(onGoingRideResponse.getData());
                    }, mapModel::onError);
*/


        }


    }


    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (currentMarker == null)
            currentMarker = mMap.addMarker(new MarkerOptions().title("Auto Carro").icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)).position(latLng));
        else
            MarkerAnimation.animateMarkerToGB(currentMarker, latLng, new LatLngInterpolator.Spherical());
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

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        ;
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


}

