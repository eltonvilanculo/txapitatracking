package mmconsultoria.co.mz.mbelamova.activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.fragment.ProgressDialog;
import mmconsultoria.co.mz.mbelamova.fragment.Response;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Trip;
import mmconsultoria.co.mz.mbelamova.model.TripStatus;
import mmconsultoria.co.mz.mbelamova.view_model.MapViewModel;
import timber.log.Timber;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationActivity extends BaseActivity {
    @BindView(R.id.notification_accept_btn)
    public Button acceptBtn;
    @BindView(R.id.notification_reject_btn)
    public Button rejectBtn;
    @BindView(R.id.notification_from)
    public TextView fromTxt;
    @BindView(R.id.notification_type)
    public TextView typeTxt;
    private MapViewModel mapModel;
    private LiveData<Response<String>> requestTripLiveData;
    private LiveData<Response> confirmTripLiveData;
    private String rideId;
    private String tripId;
    private Person person;
    private Trip trip;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        createDialog();
        if (savedInstanceState == null) {
            init();
        }
    }

    private void init() {

        mapModel = ViewModelProviders.of(this).get(MapViewModel.class);
        person = (Person) getParcelable();

        Bundle bundle = getBundle();
        String from = bundle.getString("senderName");
        String messageType = bundle.getString("messageType");
        rideId = bundle.getString("rideId");
        tripId = bundle.getString("tripId");

        fromTxt.setText("De: " + from);
        typeTxt.setText(messageType);


        LiveData<Response<Trip>> tripsLiveData = mapModel.getSingleTrip(rideId, tripId);
        tripsLiveData.observe(this, response -> {
            switch (response.getRequestResult()) {
                case SUCCESSFULL:



                    dialog.dismiss();
                    trip = response.getData();
                    trip.setId(response.getKey());

                    Timber.i("Trip: %s", trip);

                    if (trip.getStatus() == TripStatus.DRIVER_CONFIRMED) {
                        startActivity(ClientMapActivity.class, bundle, trip);
                    }

                    if (trip.getStatus() == TripStatus.IN_PROGRESS) {
                        startActivity(ClientMapActivity.class, bundle, trip);
                    }

                    if (trip.getChangedBy().equals("CLIENT")) {
                        fromTxt.setText("De: " + trip.name());
                    }

                    if (trip.getStatus() == TripStatus.CLIENT_REQUESTED) {
                        typeTxt.setText("Pedido de Viagem");
                    }

                    break;
                default:
                    Toasty.error(this, "Could not get Trip").show();
                    dialog.dismiss();
            }
        });

    }


    private void createDialog() {
        dialog = new ProgressDialog(this);
        dialog.setText("Aguarde...");
        dialog.show();
    }

    /*private void confirmTripDriver(String rideId, Trip trip) {
        trip.setStatus(TripStatus.DRIVER_CONFIRMED);
        trip.setChangedBy(revert(trip.getChangedBy()));

        confirmTripLiveData = mapModel.updateTrip(trip, rideId);
        confirmTripLiveData.observe(this, this::onRequestTripResult);
    }

    private String revert(String changedBy) {
        return changedBy.equals("CLIENT") ? "DRIVER" : "CLIENT";
    }

    private void rejectTripDriver(String rideId, Trip trip) {
        trip.setStatus(TripStatus.CLIENT_REJECTED);

        trip.setChangedBy(revert(trip.getChangedBy()));

        confirmTripLiveData = mapModel.updateTrip(trip, rideId);
        confirmTripLiveData.observe(this, this::onRequestTripResult);
    }

    private void cancelTripClient(String rideId, Trip trip) {
        trip.setStatus(TripStatus.CLIENT_CANCELED);

        confirmTripLiveData = mapModel.updateTrip(trip, rideId);
        confirmTripLiveData.observe(this, this::onRequestTripResult);
    }*/

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

    @OnClick(R.id.notification_accept_btn)
    public void accept(View view) {
        confirmTripDriver(rideId, trip);
    }

    @OnClick(R.id.notification_reject_btn)
    public void reject(View view) {
        rejectTripDriver(rideId, trip);
    }
}
