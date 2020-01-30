package mmconsultoria.co.mz.mbelamova.view_model;

import android.app.Application;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mmconsultoria.co.mz.mbelamova.activity.VolleyRequestQueuer;
import mmconsultoria.co.mz.mbelamova.cloud.CloudRepository;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.cloud.EntrySet;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.fragment.Response;
import mmconsultoria.co.mz.mbelamova.model.AvailableRide;
import mmconsultoria.co.mz.mbelamova.model.DriverAvailable;
import mmconsultoria.co.mz.mbelamova.model.Notification;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Place;
import mmconsultoria.co.mz.mbelamova.model.Review;
import mmconsultoria.co.mz.mbelamova.model.Ride;
import mmconsultoria.co.mz.mbelamova.model.Trip;
import mmconsultoria.co.mz.mbelamova.model.TripStatus;
import mmconsultoria.co.mz.mbelamova.util.LocalDataUtils;
import timber.log.Timber;

public class MapViewModel extends AuthModel {

    private MutableLiveData<List<Trip>> listOfTripRequestsLIveData;
    private List<Trip> listOfTripRequests;
    private MutableLiveData<List<AvailableRide>> driversList;
    private MutableLiveData<Response<Ride>> singleRide;
    private MutableLiveData<Response<Trip>> singleTrip;

    public MapViewModel(@NonNull Application application) {
        super(application);
        listOfTripRequests = new ArrayList<>();


        listOfTripRequestsLIveData = new MutableLiveData<>();
        driversList = new MutableLiveData<>();

        singleTrip = new MutableLiveData<>();
        singleRide = new MutableLiveData<>();

    }


    public LiveData<Person> querySingle(String userId) {
        MutableLiveData<Person> person = new MutableLiveData<>();

        new CloudRepository<>(getApplication(), Person.class)
                .setSubPath(userId)
                .attachListener()
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    person.postValue(response.getData());
                }, throwable -> {
                    person.postValue(null);
                });


        return person;
    }


    public Single<Trip> calculateTrip(DriverAvailable driver, Person customer, Place startPoint, Place endPoint, double distance) {
        Trip trip = Trip.builder(customer, startPoint, endPoint)
                .price(250)
                .driverId(driver.getDriverId())
                .distanceInKillo(distance)
                .driverName(driver.getDriverName())
                .build();

        return Single.just(trip);
    }


    public LiveData<Response> updateTrip(Trip trip, String rideId) {

        Timber.i("trip: %s , rideId: %s", trip, rideId);


        MutableLiveData<Response> liveData = new MutableLiveData<>();
        new CloudRepository<Trip>(getApplication(), Trip.class)
                .setPath("Ride")
                .setSubPath(rideId)
                .setSubPath("trips")
                .setSubPath(trip.getClientId())
                .setData(trip)
                .subscribeOn(Schedulers.io())
                .subscribe(success -> {
                    liveData.postValue(new Response()
                            .setKey(success.getKey())
                            .setRequestResult(RequestResult.SUCCESSFULL));
                }, throwable -> {
                    liveData.postValue((new Response().setRequestResult(analyzeError(throwable))));
                });

        return liveData;
    }

    public LiveData<Response<String>> requestTrip(Trip trip, String rideId) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        Timber.i("adding to ride: %s a trip: %s ", rideId, trip);


        if (TextUtils.isEmpty(rideId)) {
            liveData.postValue(new Response<String>().setRequestResult(RequestResult.ERR_OPERATION_NOT_PERMITED));
        }


        new CloudRepository<Trip>(getApplication(), Trip.class)
                .setPath("Ride")
                .setSubPath(rideId)
                .setSubPath("trips")
                .setChildData(trip, trip.getClientId())
                .subscribeOn(Schedulers.io())
                .subscribe(success -> {
/*
                    SubData data = new SubData();
                    data.setTripId(success.getKey());
                    data.setRideId(rideId);
                    data.setDistance(trip.getDistanceInKilo());
                    data.setSenderId(trip.getClient().getId());
                    data.setSenderName(trip.getClient().retrieveFullName());
                    data.setStartLatitude(trip.getStartPoint().getLatitude());
                    data.setStartLongitude(trip.getStartPoint().getLongitude());
                    data.setEndLatitude(trip.getEndPoint().getLatitude());
                    data.setEndLongitude(trip.getEndPoint().getLongitude());
                    data.setReceiverRole(SubData.DRIVER_ROLE);
                    data.setMessageType(SubData.REQUEST_RIDE_TYPE);

                    Notification notification = new Notification();
                    notification.setTo(trip.getDriverId());
                    sendNotification(notification)
                            .execute(next -> {
                                liveData.postValue(new Response()
                                        .setKey(success.getKey())
                                        .setRequestResult(RequestResult.SUCCESSFULL));
                            }, error -> {
                                liveData.postValue(new Response<String>()
                                        .setKey(success.getKey())
                                        .setRequestResult(RequestResult.NOTIFICATION_NOT_SENT));
                            });*/


                    liveData.postValue(new Response()
                            .setKey(success.getKey())
                            .setRequestResult(RequestResult.SUCCESSFULL));

                }, throwable -> {
                    liveData.postValue(new Response<String>().setRequestResult(analyzeError(throwable)));
                });

        return liveData;
    }


    public VolleyRequestQueuer.Executor sendNotification(Notification notification) {
        return VolleyRequestQueuer.init(getApplication()).sendDataNotification(notification);
    }

    public Observable<Response<Ride>> checkRideDriverRequest(String liftId, String name, String value) {
        return new CloudRepository<Ride>(getApplication(), Ride.class)
                .setSubPath(DatabaseValue.Requests.name())
                .setSubPath(liftId)
                .deepEqualsToQuery(Collections.singletonList(EntrySet.from(name, value)))
                .subscribeOn(Schedulers.io());
    }


    public Single<Response<String>> createRide(Ride ride) {
        Timber.i("Ride: %s, on thread: %s", ride, Thread.currentThread().getName());


        return new CloudRepository<>(getApplication(), Ride.class)
                .setChildData(ride)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(response -> {
                    if (response.getRequestResult() == RequestResult.SUCCESSFULL) {

                        /*OnGoingRide data = OnGoingRide.fromUser(ride);
                        Timber.i("ride: %s, OnGoingRide: %s on thread: %s", ride, data, Thread.currentThread().getName());
                        new CloudRepository<>(getApplication(), OnGoingRide.class)
                                .setChildData(data, response.getData())
                                .subscribe(success -> {
                                    LocalDataUtils.fromUser(getApplication()).saveLastRideData(ride);
                                }, throwable -> {
                                    Timber.e(throwable, "error on the building");
                                });*/
                    }
                });

    }


    public Single<Response<String>> cancelRide(Ride ride) {
        ride.setStatus(TripStatus.CLIENT_CANCELED);
        return new CloudRepository<>(getApplication(), Ride.class)
                .setData(ride)
                .subscribeOn(Schedulers.io());
    }

    public MutableLiveData<Response<String>> finishRide(Ride ride, double moneyAvailable) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        ride.setStatus(TripStatus.FINISHED);

        for (Trip trip : ride.getTrips().values()) {
            if (trip.getStatus() == TripStatus.IN_PROGRESS) {
                trip.setStatus(TripStatus.FINISHED);
                trip.setChangedBy("DRIVER");
            } else {
                trip.setStatus(TripStatus.CLIENT_CANCELED);
            }
        }

        Timber.d("ride: %s", ride);

        new CloudRepository<Ride>(getApplication(), Ride.class)
                .setSubPath(ride.getId())
                .setData(ride)
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                        double value = 0;
                        for (Trip trip : ride.getTrips().values()) {
                            if (trip.getStatus() == TripStatus.FINISHED) {
                                value = trip.getPrice();
                            }
                        }

                        updateUsersBalance(ride.getDriverId(), (moneyAvailable + value))
                                .subscribe(success -> {
                                    if (success.getRequestResult() == RequestResult.SUCCESSFULL) {
                                        liveData.postValue(new Response<String>().setNextToken(next.getKey()).setRequestResult(RequestResult.SUCCESSFULL));
                                    }
                                }, throwable -> {
                                    liveData.postValue(new Response<String>().setRequestResult(RequestResult.ERR_UNKNOWN));
                                });
                    }
                }, throwable -> {
                    liveData.postValue(new Response<String>().setRequestResult(RequestResult.ERR_UNKNOWN));
                });

        return liveData;
    }

    public LiveData<Response<String>> finishTrip(Trip trip) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        trip.setStatus(TripStatus.FINISHED);
        new CloudRepository<>(getApplication(), Ride.class)
                .setSubPath(trip.getRideId())
                .setSubPath("trips")
                .setSubPath(trip.getClientId())
                .updateField("status", TripStatus.FINISHED)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> liveData.postValue(next),
                        throwable -> liveData.postValue(new Response<String>().setRequestResult(analyzeError(throwable))));

        return liveData;
    }

    public LiveData<Response<String>> cancelTripClient(Trip trip) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        trip.setStatus(TripStatus.FINISHED);
        new CloudRepository<>(getApplication(), Ride.class)
                .setSubPath(trip.getRideId())
                .setSubPath("trips")
                .setSubPath(trip.getClientId())
                .updateField("status", TripStatus.CLIENT_CANCELED)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> liveData.postValue(next),
                        throwable -> liveData.postValue(new Response<String>().setRequestResult(analyzeError(throwable))));

        return liveData;
    }

    public LiveData<Response<String>> cancelTripDriver(Trip trip) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        trip.setStatus(TripStatus.FINISHED);
        new CloudRepository<>(getApplication(), Ride.class)
                .setSubPath(trip.getRideId())
                .setSubPath("trips")
                .setSubPath(trip.getClientId())
                .updateField("status", TripStatus.DRIVER_CANCELED)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> liveData.postValue(next),
                        throwable -> liveData.postValue(new Response<String>().setRequestResult(analyzeError(throwable))));

        return liveData;
    }

    public LiveData<Response<String>> giveUpTripDriver(Trip trip) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        trip.setStatus(TripStatus.FINISHED);
        new CloudRepository<>(getApplication(), Ride.class)
                .setSubPath(trip.getRideId())
                .setSubPath("trips")
                .setSubPath(trip.getClientId())
                .updateField("status", TripStatus.DRIVER_GIVE_UP)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> liveData.postValue(next),
                        throwable -> liveData.postValue(new Response<String>().setRequestResult(analyzeError(throwable))));

        return liveData;
    }

    public LiveData<Response<String>> giveUpTripClient(Trip trip) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        trip.setStatus(TripStatus.FINISHED);
        new CloudRepository<>(getApplication(), Ride.class)
                .setSubPath(trip.getRideId())
                .setSubPath("trips")
                .setSubPath(trip.getClientId())
                .updateField("status", TripStatus.CLIENT_GIVE_UP)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> liveData.postValue(next),
                        throwable -> liveData.postValue(new Response<String>().setRequestResult(analyzeError(throwable))));

        return liveData;
    }

    @Override
    public void onError(Throwable throwable) {
        super.onError(throwable);
    }


    public LiveData<List<Trip>> getListOfTripRequests() {
        return listOfTripRequestsLIveData;
    }

    public void requestListOfTripsRequests(String driverId) {
        new CloudRepository<Trip>(getApplication(), Trip.class)
                .setSubPath(DatabaseValue.Requests.name())
                .setSubPath(driverId)
                .attachListener()
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if (response.hasError()) {
                        Timber.e(response.getRequestResult() + "");
                    } else {
                        if (response.getMovement() == CloudRepository.DatabaseMovement.Removal) {
                            listOfTripRequests.remove(response.getData());
                            listOfTripRequestsLIveData.postValue(listOfTripRequests);
                        }

                        if (response.getMovement() == CloudRepository.DatabaseMovement.Update) {
                            listOfTripRequests.remove(response.getData());
                            listOfTripRequests.add(response.getData());
                            listOfTripRequestsLIveData.postValue(listOfTripRequests);
                        }

                        if (response.getMovement() == CloudRepository.DatabaseMovement.Addition) {
                            listOfTripRequests.add(response.getData());
                            listOfTripRequestsLIveData.postValue(listOfTripRequests);
                        }
                    }
                }, this::onError);
    }


    public Single<Response<String>> notifyClient(String key, String clientId) {
        return new CloudRepository<String>(getApplication(), String.class)
                .setSubPath(DatabaseValue.CLIENT.name())
                .setChildData(key, clientId)
                .subscribeOn(Schedulers.io());
    }


    public LiveData<Response<Trip>> getSingleTrip(String rideId, String tripId) {
        Timber.i("searching trip with ride: %s, trip: %s", rideId, tripId);
        MutableLiveData<Response<Trip>> single = new MutableLiveData<>();

        if (TextUtils.isEmpty(rideId) || TextUtils.isEmpty(tripId)) {
            single.postValue(new Response<Trip>().setRequestResult(RequestResult.INVALID_PARAMETERS));
        } else {

            new CloudRepository<Trip>(getApplication(), Trip.class)
                    .setPath("Ride")
                    .setSubPath(rideId)
                    .setSubPath("trips")
                    .setSubPath(tripId)
                    .attachListener()
                    .subscribeOn(Schedulers.io())
                    .subscribe(next -> {
                        if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                            singleTrip.postValue(next);
                        }
                    }, throwable -> {
                        RequestResult requestResult = analyzeError(throwable);
                        single.postValue(new Response<Trip>().setRequestResult(requestResult));
                    });
        }
        return singleTrip;
    }

    public LiveData<Response<String>> reviewDriver(Review review, String driverId) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        new CloudRepository<>(getApplication(), Review.class)
                .setSubPath(driverId)
                .setSubPath("Driver")
                .setChildData(review)
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                        liveData.postValue(new Response<String>().setKey(next.getKey()).setRequestResult(RequestResult.SUCCESSFULL));
                    }
                }, throwable -> {
                    liveData.postValue(new Response<String>().setRequestResult(RequestResult.ERR_UNKNOWN));
                });

        return liveData;
    }

    public LiveData<Response<String>> scheduleTrip(Trip trip) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        new CloudRepository<>(getApplication(), Trip.class)
                .setPath("ScheduleClient")
                .setChildData(trip)
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                        liveData.postValue(new Response<String>().setKey(next.getKey()).setRequestResult(RequestResult.SUCCESSFULL));
                    }
                }, throwable -> {
                    liveData.postValue(new Response<String>().setRequestResult(RequestResult.ERR_UNKNOWN));
                });

        return liveData;
    }

    public LiveData<Response<String>> reviewClient(Review review, String driverId) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        new CloudRepository<>(getApplication(), Review.class)
                .setSubPath(driverId)
                .setSubPath("Driver")
                .setChildData(review)
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                        liveData.postValue(new Response<String>().setKey(next.getKey()).setRequestResult(RequestResult.SUCCESSFULL));
                    }
                }, throwable -> {
                    liveData.postValue(new Response<String>().setRequestResult(RequestResult.ERR_UNKNOWN));
                });

        return liveData;
    }


    public LiveData<Response<String>> startRide(Ride ride) {
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        for (Trip trip : ride.getTrips().values()) {
            if (trip.getStatus() == TripStatus.SCHEDULED) {
                trip.setStatus(TripStatus.IN_PROGRESS);
            } else {
                trip.setStatus(TripStatus.CLIENT_CANCELED);
            }
        }

        new CloudRepository<Ride>(getApplication(), Ride.class)
                .setSubPath(ride.getId())
                .setData(ride)
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                        liveData.postValue(new Response<String>().setKey(next.getKey()).setRequestResult(RequestResult.SUCCESSFULL));
                    }
                }, throwable -> {
                    liveData.postValue(new Response<String>().setRequestResult(RequestResult.ERR_UNKNOWN));
                });

        return liveData;
    }

    public void saveRide(Ride ride) {
        LocalDataUtils.from(getApplication()).saveObject(DatabaseValue.LAST_RIDE.name(), ride);
    }

    public MutableLiveData<Response<Ride>> getSingleRide(String id) {
        if (TextUtils.isEmpty(id)) {
            singleRide.postValue(new Response<Ride>().setRequestResult(RequestResult.INVALID_PARAMETERS));
            Timber.d("id is empty");
            return singleRide;
        }

        new CloudRepository<Ride>(getApplication(), Ride.class)
                .setSubPath(id)
                .attachListener()
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    Timber.d("Response: %s", next);

                    if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                        singleRide.postValue(next.setRequestResult(RequestResult.SUCCESSFULL));
                    }
                }, throwable -> {
                    singleRide.postValue(new Response<Ride>().setRequestResult(RequestResult.ERR_UNKNOWN));
                });

        return singleRide;

    }

    public LiveData<Response<String>> matchRoute(Trip trip) {
        Timber.i(" trip: %s", trip);
        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();


        new CloudRepository<Trip>(getApplication(), Trip.class)
                .setPath("Requests")
                .setChildData(trip)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> {
                    if (next.getRequestResult() == RequestResult.SUCCESSFULL) {
                        liveData.postValue(next);
                    }
                }, throwable -> {
                    RequestResult requestResult = analyzeError(throwable);
                    liveData.postValue(new Response<String>().setRequestResult(requestResult));
                });

        return liveData;


    }

    public LiveData<Response<String>> startTrip(Trip trip) {
        Timber.d("trip: %s", trip);

        MutableLiveData<Response<String>> liveData = new MutableLiveData<>();

        trip.setStatus(TripStatus.IN_PROGRESS);
        new CloudRepository<>(getApplication(), Ride.class)
                .setSubPath(trip.getRideId())
                .setSubPath("trips")
                .setSubPath(trip.getClientId())
                .updateField("status", TripStatus.IN_PROGRESS)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> liveData.postValue(next),
                        throwable -> liveData.postValue(new Response<String>().setRequestResult(analyzeError(throwable))));

        return liveData;

    }

    public void setDriversList(List<AvailableRide> list) {
        this.driversList.postValue(list);
    }

    public LiveData<List<AvailableRide>> getDriversList() {
        return driversList;
    }

    public void saveTrip(Trip trip) {
        LocalDataUtils.from(getApplication()).saveObject(DatabaseValue.LAST_TRIP.name(), trip);
    }

    public LiveData<Response<Trip>> getLastTrip() {
        Trip trip = LocalDataUtils.from(getApplication()).getObject(DatabaseValue.LAST_TRIP.name(), Trip.class);
        if (trip == null) {
            return getSingleTrip("", "");
        }
        return getSingleTrip(trip.getId(), trip.getId());
    }

    public LiveData<Response<Ride>> getLastRide() {
        Ride ride = LocalDataUtils.from(getApplication()).getObject(DatabaseValue.LAST_RIDE.name(), Ride.class);
        if (ride == null) {
            return getSingleRide("");
        }
        return getSingleRide(ride.getId());
    }


}
