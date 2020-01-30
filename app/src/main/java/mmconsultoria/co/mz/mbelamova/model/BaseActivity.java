package mmconsultoria.co.mz.mbelamova.model;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import es.dmoral.toasty.Toasty;
import io.reactivex.Single;
import mmconsultoria.co.mz.mbelamova.fragment.Response;
import mmconsultoria.co.mz.mbelamova.service.FirebaseNotificationService;
import mmconsultoria.co.mz.mbelamova.view_model.MapViewModel;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();
    private String lastUsedFragmentTag;
    private final String INTENT_EXTRA_BUNDLE = "Bundle";
    private final String INTENT_EXTRA_PARCELABLE = "Parcelable";
    private LiveData<Response> confirmTripLiveData;
    protected MutableLiveData<Trip> notificationTrip = new MutableLiveData<>();
    private MapViewModel mapModel;
    private AlertDialog dialog;
    private OnTripUpdatedListener onTripUpdatedListener;
    private State state;

    public void setOnTripUpdatedListener(OnTripUpdatedListener onTripUpdatedListener) {
        this.onTripUpdatedListener = onTripUpdatedListener;
    }

    public interface OnTripUpdatedListener {
        void onUpdate(String rideId, Trip trip);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        state = State.FOREGROUND;

    }

    protected void createdDialog(Bundle bundle) {
        String senderName = bundle.getString("senderName");
        String messageType = bundle.getString("messageType");
        String rideId = bundle.getString("rideId");
        String tripId = bundle.getString("tripId");
        String status = bundle.getString("status");
        String driverId = bundle.getString("driverId");
        String clientId = bundle.getString("clientId");
        String clientName = bundle.getString("clientName");
        String driverName = bundle.getString("driverName");
        String rides = bundle.getString("rides");
        String purpose = bundle.getString("purpose");

        if (TextUtils.isEmpty(purpose)) {
            Timber.e("Empty porpose");
            return;
        }

        if (purpose.equals(FirebaseNotificationService.PORPOSE_STATUS_MESSAGE)) {
            if (status.equals(TripStatus.FINISHED.name())) {
                Trip empty = Trip.empty();
                empty.setStatus(TripStatus.FINISHED);
                empty.setClientName(clientName);
                empty.setClientId(clientId);
                empty.setDriverId(driverId);
                empty.setDriverName(driverName);

                if (onTripUpdatedListener != null) {
                    onTripUpdatedListener.onUpdate(rideId, empty);
                }
                return;
            }


            Timber.d("Trip data: %s", bundle.keySet());

            mapModel = ViewModelProviders.of(this).get(MapViewModel.class);

            LiveData<Response<Trip>> tripsLiveData = mapModel.getSingleTrip(rideId, tripId);
            tripsLiveData.observe(this, response -> {
                switch (response.getRequestResult()) {
                    case SUCCESSFULL:
                        Trip trip = response.getData();
                        trip.setId(response.getKey());
                        Timber.i("Trip: %s", trip);

                        if (onTripUpdatedListener != null) {
                            onTripUpdatedListener.onUpdate(rideId, trip);
                        }

                   /* if (TripStatus.CLIENT_REQUESTED == trip.getStatus()) {
                        builder.setNegativeButton("Recusar", (dialog, which) -> {
                            rejectTripDriver(rideId, trip);
                        }).setPositiveButton("Aceitar", (dialog, which) -> {
                            confirmTripDriver(rideId, trip);
                        }).setCancelable(false).show();

                    }*/

                    /*if (TripStatus.DRIVER_CONFIRMED == trip.getStatus()) {
                        builder.setTitle("Viagem Confirmada")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                }).setCancelable(false).show();

                    }

                    if (TripStatus.DRIVER_REJECTED == trip.getStatus()) {
                        builder.setTitle("Pedido de Viagem rejeitado")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                }).setCancelable(false).show();

                    }*/


                        break;
                    default:
                        Toasty.error(this, "Could not get Trip").show();
                        break;
                }
            });
        }

        if (purpose.equals(FirebaseNotificationService.PORPOSE_PAYMENT)) {
            return;
        }




    }

    public void confirmTripDriver(String rideId, Trip trip) {
        trip.setStatus(TripStatus.DRIVER_CONFIRMED);
        trip.setChangedBy("DRIVER");

        confirmTripLiveData = mapModel.updateTrip(trip, rideId);
        confirmTripLiveData.observe(this, this::onRequestTripResult);
    }





    public void rejectTripDriver(String rideId, Trip trip) {
        trip.setStatus(TripStatus.DRIVER_REJECTED);

        trip.setChangedBy("DRIVER");

        confirmTripLiveData = mapModel.updateTrip(trip, rideId);
        confirmTripLiveData.observe(this, this::onRequestTripResult);
    }

    public void cancelTripClient(String rideId, Trip trip) {
        trip.setStatus(TripStatus.CLIENT_CANCELED);
        trip.setChangedBy("CLIENT");

        confirmTripLiveData = mapModel.updateTrip(trip, rideId);
        confirmTripLiveData.observe(this, this::onRequestTripResult);
    }

    public void cancelTripDriver(String rideId, Trip trip) {
        trip.setStatus(TripStatus.DRIVER_CANCELED);
        trip.setChangedBy("DRIVER");

        confirmTripLiveData = mapModel.updateTrip(trip, rideId);
        confirmTripLiveData.observe(this, this::onRequestTripResult);
    }


    public void startMyActivity(Class<?> target) {
        startActivity(new Intent(this, target));
    }

    public <activity extends BaseActivity> void startActivity(Class<activity> target, @Nullable Bundle bundle, @Nullable Parcelable data) {
        Intent intent = new Intent(this, target);

        if (bundle != null) {
            intent.putExtra(INTENT_EXTRA_BUNDLE, bundle);
        }

        if (data != null) {
            intent.putExtra(INTENT_EXTRA_PARCELABLE, data);
        }

        startActivity(intent);
    }

    public Bundle getBundle() {
        return getIntent().getBundleExtra(INTENT_EXTRA_BUNDLE);
    }

    public Parcelable getParcelable() {
        return getIntent().getParcelableExtra(INTENT_EXTRA_PARCELABLE);
    }

    public <Frag extends BaseFragment> void swapFragment(@IdRes int container, @NonNull Frag fragment, @Nullable String tag) {
        if (tag == null || tag.isEmpty())
            tag = fragment.getTAG();

        lastUsedFragmentTag = tag;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(container, fragment, tag)
                .commit();
    }

    public String getText(@NonNull TextView textSource) {
        return textSource.getText().toString().trim();
    }

    public <Frag extends BaseFragment> void swapFragmentAndAddToBackStack(@IdRes int container, @NonNull Frag fragment, @Nullable String tag, @Nullable String stackName) {
        if (tag == null || tag.isEmpty())
            tag = fragment.getTAG();

        lastUsedFragmentTag = tag;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(container, fragment, tag)
                .addToBackStack(stackName)
                .commit();
    }

    public Double getNumber(TextView textView) {
        Double number;

        String text = getText(textView);

        number = Double.parseDouble(text);


        return number;
    }


    public void callImageFromStorage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Escolha a Imagem"), requestCode);
    }

    public void changeFont(String path, TextView target) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), path);
        target.setTypeface(typeface);
    }


    public String getLastUsedFragmentTag() {
        return lastUsedFragmentTag;
    }

    public Single<Boolean> subscribeToNotification() {
        return Single.create(emitter -> {
            try {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    Timber.i("isTaskSuccessful: %s", task.isSuccessful());
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();

                        subscribeToNotification(token).subscribe(emitter::onSuccess, emitter::onError);

                    } else {
                        emitter.onSuccess(Boolean.FALSE);
                    }
                });
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }


    public Single<Boolean> unsubscribeToNotification(String topic) {
        return Single.create(emitter -> {
            try {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                        .addOnCompleteListener(task -> {

                            Timber.i("TaskResult: %s", task.isSuccessful());
                            if (task.isSuccessful()) {
                                emitter.onSuccess(true);
                            } else {
                                emitter.onSuccess(false);
                            }


                        });
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Single<Boolean> subscribeToNotification(String topic) {
        return Single.create(emitter -> {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                        .addOnCompleteListener(task -> {
                            Timber.i("TaskResult: %s", task.isSuccessful());
                            Timber.i(task.getException(), "TaskException: ");
                            if (task.isSuccessful()) {
                                emitter.onSuccess(true);
                            } else {
                                emitter.onSuccess(false);
                            }
                        });
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    private void onRequestTripResult(Response<String> stringResponse) {
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


/*    public void sendNotification(String title, String subject, SubData data, String to) {
        VolleyRequestQueuer
                .init(this)
                .sendDataNotification(title, subject, data, to)
                .execute(response -> Timber.d("response: %s", response), Timber::d);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        state = State.FOREGROUND;
        mInstance = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        state = State.BACKGROUND;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d(TAG + ": Foreground");
        isAppInBackground(State.FOREGROUND);
    }

    ///////////////////////////////////////////////


    @Override
    protected void onPause() {
        super.onPause();
        Timber.d(TAG + ": Background");
        isAppInBackground(State.BACKGROUND);
    }


    ///////////////////////////////////////////////

    public enum State {
        BACKGROUND, FOREGROUND
    }


    private void isAppInBackground(State state) {
        this.state = state;
    }

    public synchronized State getCurrentState() {
        return state;
    }


    private static BaseActivity mInstance;

    public static BaseActivity getInstance() {
        return mInstance;
    }

}
