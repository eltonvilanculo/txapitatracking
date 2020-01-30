package mmconsultoria.co.mz.mbelamova.view_model;

import android.app.Application;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import mmconsultoria.co.mz.mbelamova.cloud.CloudRepository;
import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.cloud.EntrySet;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.fragment.Response;
import mmconsultoria.co.mz.mbelamova.model.DriverData;
import mmconsultoria.co.mz.mbelamova.model.DriverLicence;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Vehicle;
import mmconsultoria.co.mz.mbelamova.service.ServerUpdater;
import mmconsultoria.co.mz.mbelamova.util.LocalDataUtils;
import timber.log.Timber;

import static java.lang.String.valueOf;

public class AuthModel extends AndroidViewModel {
    private AuthService authService;
    private MutableLiveData<Person> userData;


    private AuthService.AuthResult verificationType;
    private String userId;
    private MutableLiveData<AuthService.AuthResult> signInStatus;

    public AuthModel(@NonNull Application application) {
        super(application);
        authService = AuthService.getInstance();
        userData = new MutableLiveData<>();
        signInStatus = new MutableLiveData<>();


    }

    public void onError(Throwable throwable) {
        Timber.e(throwable);
    }

    public void getCurrentUser() {
        Person person = loadUserData();
        Timber.d("%s", person);

        if (person == null) {
            queryCurrentUser(authService.getUserId());
        } else {
            updateUserLiveData(person);
            queryCurrentUser(authService.getUserId());
        }
    }

    private void updateUserLiveData(Person person) {
        Timber.d("  person: %s", person);
        userData.postValue(person);
        LocalDataUtils.from(getApplication()).saveObject(DatabaseValue.AuthData.name(),person);
    }

    private String notificationId;

    private void queryCurrentUser(String userId) {
        this.userId = userId;
        Timber.d(valueOf(userId));

        if (userId == null || userId.isEmpty()) {
            return;
        }

        Timber.i("UserId: %s", userId);
        Timber.i("getMessaging Token: %s", FirebaseInstanceId.getInstance().getToken());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                InstanceIdResult result = task.getResult();
                notificationId = task.getResult().getToken();
                Timber.i("FirebaseInstanceId: %s \nToken: %s", result.getId(), result.getToken());
            } else {
                Timber.i("could not reproduce instance id");
            }
        });

        CloudRepository<Person> repository = new CloudRepository<>(getApplication(), Person.class);

        repository.setSubPath(userId);

        repository.attachListener().subscribe((response) -> {
            if (response.getMovement() != CloudRepository.DatabaseMovement.Removal) {
                Person data = response.getData();
                data.setId(response.getKey());
                saveUserData(data);
                updateUserLiveData(data);

                if (notificationId.equals(data.getNotificationId()))
                    return;
                repository.updateField("notificationId", notificationId)
                        .subscribe(next -> Timber.i("notificationId updated"), throwable -> Timber.i("notificationId not updated"));

            }
        }, this::onError);
    }

    public Observable<AuthService.AuthResult> signIn(String phoneNumber, FragmentActivity activity) {
        return authService.signIn(activity, phoneNumber)
                .subscribeOn(Schedulers.io());
    }

    public Single<AuthService.AuthResult> signUp(Person person, String imageUri, FragmentActivity activity) {
        return Single.<AuthService.AuthResult>create(emitter -> {
            signIn(person.getPhoneNumber(), activity)
                    .doAfterNext(authResult -> {
                        recordUserdataOnCloud(person, imageUri)
                                .subscribe(emitter::onSuccess, emitter::onError);
                    });
        }).subscribeOn(Schedulers.io());

    }

    public Single<AuthService.AuthResult> recordUserdataOnCloud(Person person, String imageUri) {
        person.setId(userId);

        Timber.d(valueOf(person));

        return Single.<AuthService.AuthResult>create(
                emitter -> {
                    (new CloudRepository<>(getApplication(), Person.class))
                            .setChildData(imageUri, person, authService.getUserId()).subscribe(taskError -> {
                        Timber.d(person.toString());
                        emitter.onSuccess(AuthService.AuthResult.USER_CREATED);
                    }, emitter::onError);
                }).subscribeOn(Schedulers.io());
    }


    public void signUp(Person person, String password) {
        signInStatus.postValue(AuthService.AuthResult.PROCCESSIGN);

        authService.createUserWithEmailAndPassword(person, password)
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    Timber.d("userId: %s", next);
                    new CloudRepository<>(getApplication(), Person.class)
                            .setChildData(person, next)
                            .subscribe(subscribe -> {
                                Timber.d("Created user at firebaseAuth");
                                person.setId(subscribe.getData());

                                LocalDataUtils.from(getApplication()).putPerson(person);

                                signInStatus.postValue(AuthService.AuthResult.USER_CREATED.setKey(subscribe.getKey()));
                                queryCurrentUser(subscribe.getKey());
                            }, throwable -> {
                                Timber.i(throwable);
                                signInStatus.postValue(AuthService.AuthResult.ERROR);
                            });
                }, throwable -> {
                    Timber.i(throwable);
                    signInStatus.postValue(AuthService.AuthResult.ERROR);
                });
    }

    public void signInUser(String email, String password) {
        signInStatus.postValue(AuthService.AuthResult.PROCCESSIGN);

        authService.signInWithEmailAndPassword(email, password)
                .observeOn(Schedulers.io())
                .subscribe(next -> {
                    signInStatus.postValue(AuthService.AuthResult.SIGNED_IN.setKey(next));

                    new CloudRepository<>(getApplication(), Person.class)
                            .deepEqualsToQuery(Collections.singletonList(EntrySet.from("email", email)))
                            .subscribe(personResponse -> {
                                Person data = personResponse.getData();
                                data.setId(personResponse.getKey());
                                LocalDataUtils.from(getApplication()).putPerson(data);
                            }, throwable -> {
                                Timber.d(throwable);
                                signInStatus.postValue(AuthService.AuthResult.USER_RECORD_NOT_FOUND_ON_CLOUD.setKey(next));

                            });
                }, throwable -> {
                    Timber.d(throwable);
                    signInStatus.postValue(AuthService.AuthResult.ERROR);

                    signInStatus.postValue(checkAuthError(throwable));
                });
    }

    private AuthService.AuthResult checkAuthError(Throwable throwable) {
        Timber.wtf(throwable, "Auth error: ");


        if (throwable instanceof FirebaseApiNotAvailableException) {

        }

        if (throwable instanceof FirebaseNetworkException) {
            return AuthService.AuthResult.ERR_NETWORK;
        }

        if (throwable instanceof FirebaseAuthInvalidCredentialsException) {
            return AuthService.AuthResult.ERR_INVALID_CREDENTIALS;
        }

        if (throwable instanceof FirebaseAuthUserCollisionException) {
            return AuthService.AuthResult.USER_COLLISOION;
        }

        if (throwable instanceof FirebaseAuthEmailException) {

        }

        if (throwable instanceof FirebaseNoSignedInUserException) {
            return AuthService.AuthResult.ERR_REAUTH_REQUIRED;
        }

        if (throwable instanceof FirebaseAuthRecentLoginRequiredException) {
            return AuthService.AuthResult.ERR_REAUTH_REQUIRED;
        }


        return AuthService.AuthResult.ERR_UNKNOWN;

    }

    public Single<AuthService.AuthResult> sendConfirmCode(String code) {
        return authService.verifySmsCode(code)
                .subscribeOn(Schedulers.io());
    }

    public Single<AuthService.AuthResult> searchUserByPhoneNumber(String phoneNumber) {
        Timber.d(phoneNumber);
        return Single.<AuthService.AuthResult>create(emitter -> {

            DatabaseReference dataRef = FirebaseDatabase.getInstance()
                    .getReference("Person");
            dataRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Timber.d("Database_key: " + dataSnapshot.getKey() + " - " + valueOf(dataSnapshot.getValue()));
                    Person value = dataSnapshot.getValue(Person.class);
                    if (value != null) {


                        String id = dataSnapshot.getChildren().iterator().next().getKey();
                        value.setId(id);

                        Timber.d(value.toString());
                        userId = id;
                        saveUserData(value);
                        emitter.onSuccess(AuthService.AuthResult.USER_EXISTS);

                    } else emitter.onSuccess(AuthService.AuthResult.USER_DOES_NOT_EXISTS);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());
                }
            });

        }).doAfterSuccess(authResult -> queryCurrentUser(userId)).subscribeOn(Schedulers.io());

    }


    public LiveData<Person> getUser() {
        return userData;
    }


    private void saveUserData(Person person) {
        FirebaseMessaging.getInstance()
                .subscribeToTopic(person.getId())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.i("subscribed to FCM topic");
                    } else {
                        Timber.i("could not subscribe to FCM topic");
                    }
                });

        ServerUpdater.start().userId(person.getId()).sendWith(getApplication());
        LocalDataUtils.from(getApplication()).putPerson(person);
    }



    public Single<AuthService.AuthResult> isUserSignedIn() {
        return Single.<AuthService.AuthResult>create(emitter -> {

            if (authService.isUserSignedIn()) {
                AuthService.AuthResult signedIn = AuthService.AuthResult.SIGNED_IN;
                /*Timber.d("UserState: %s", signedIn);
                Person person = loadUserData();


                Timber.d("UserData: %s", person);
                if (person != null) {
                    Timber.d(person.toString());
                    signedIn.setKey(person.getId());
                    updateUserLiveData(person);
                } else {
                    emitter.onSuccess(AuthService.AuthResult.SIGN_UP_REQUIRED);
                }*/

                emitter.onSuccess(signedIn);
            } else emitter.onSuccess(AuthService.AuthResult.SIGNED_OUT);

        }).subscribeOn(Schedulers.io());


    }

    private Person loadUserData() {
        return LocalDataUtils.from(getApplication()).getObject(DatabaseValue.AuthData.name(), Person.class);
    }


    public AuthService.AuthResult getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(AuthService.AuthResult verificationType) {
        this.verificationType = verificationType;
    }

    public Single<Response> updateUsersBalance(String personId, double money) {
        return new CloudRepository<Person>(getApplication(), Person.class)
                .setSubPath(personId)
                .updateField("moneyAvailable", money);
    }

    public void signOut() {
        authService.signOut();
    }

    public LiveData<AuthService.AuthResult> getSignInStatus() {
        return signInStatus;
    }

    public MutableLiveData<AuthService.AuthResult> updateDriverData(Vehicle vehicle, DriverLicence licence) {
        MutableLiveData<AuthService.AuthResult> authStatus = new MutableLiveData<>();

        new CloudRepository<>(getApplication(), DriverData.class)
                .setPath("Person")
                .setSubPath(authService.getUserId())
                .setSubPath("driverData")
                .setData(new DriverData(vehicle, licence))
                .subscribeOn(Schedulers.io())
                .subscribe(next -> {
                    authStatus.postValue(AuthService.AuthResult.USER_UPDATED.setKey(next.getData()));
                }, throwable -> {
                    authStatus.postValue(analyzeAuthError(throwable));

                });

        return authStatus;
    }

    protected RequestResult analyzeError(Throwable throwable) {
        Timber.e(throwable);
        return RequestResult.ERR_UNKNOWN;

    }

    protected AuthService.AuthResult analyzeAuthError(Throwable throwable) {
        Timber.e(throwable);
        return AuthService.AuthResult.ERR_UNKNOWN;

    }


    public LiveData<Response<Person>> getSingleUser(String userId) {
        MutableLiveData<Response<Person>> personData = new MutableLiveData<>();

        new CloudRepository<>(getApplication(), Person.class)
                .setSubPath(userId)
                .attachListener()
                .subscribe(personData::postValue,
                        throwable -> {
                            personData.postValue(new Response<Person>()
                                    .setRequestResult(analyzeError(throwable))
                            );
                        });

        return personData;
    }
}