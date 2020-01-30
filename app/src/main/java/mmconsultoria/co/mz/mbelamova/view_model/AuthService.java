package mmconsultoria.co.mz.mbelamova.view_model;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.model.Person;
import timber.log.Timber;

public class AuthService {

    private static AuthService ourInstance;
    public final String TAG = getClass().getSimpleName();


    private Flowable<String> userId = Flowable.empty();

    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private volatile String verificationId;

    private PhoneAuthProvider.ForceResendingToken resendToken;
    private Emitter<String> userIdEmitter;
    private String notificationId;


    public AuthService() {
        this.auth = FirebaseAuth.getInstance();

    }

    public static AuthService getInstance() {
        if (ourInstance == null) {
            ourInstance = new AuthService();
        }

        return ourInstance;
    }

    public boolean isUserSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public boolean login() {
        FirebaseUser currentUser = auth.getCurrentUser();


        if (currentUser != null) {
            userId = userId.map(text -> currentUser.getUid());
            return true;
        }

        return false;
    }

    public String getUserId() {
        if (auth.getCurrentUser() != null)
            return auth.getCurrentUser().getUid();

        return "";
    }

    public Observable<AuthResult> signIn(final FragmentActivity activity, @NonNull String phoneNumber) {
        return Observable.<AuthResult>create(emitter -> {

            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Timber.d("OnVerificationComplete");


                    signInWithPhoneAuthCredential(credential)
                            .subscribe(authResult -> {
                                emitter.onNext(authResult);
                                emitter.onComplete();

                            }, emitter::onError);


                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Timber.w(e, "onVerificationFailed");
                    AuthResult authResult = AuthResult.UNKNOWN;
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        authResult = AuthResult.ERR_INVALID_CREDENTIALS;
                        Timber.w(e, "onVerificationFailed");
                    }
                    if (e instanceof FirebaseTooManyRequestsException) {
                        authResult = AuthResult.TO_MANY_LOGIN_REQUEST;
                    }

                    if (e instanceof FirebaseNetworkException) {
                        authResult = AuthResult.ERR_NETWORK;
                    } else {
                        Timber.e("onVerificationFailed() called with: e = [" + e.getMessage() + "]");
                    }

                    emitter.onNext(authResult);

                }

                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the c,ode and then construct a credential
                    // by combining the code with a verification ID.
                    Timber.d("onCodeSent: %s", verificationId);

                    emitter.onNext(AuthResult.CODE_SENT);
                    // Save verification ID and resending token so we can use them later
                    AuthService.this.verificationId = verificationId;
                    AuthService.this.resendToken = token;
                }
            };

            PhoneAuthProvider.getInstance(auth)
                    .verifyPhoneNumber(phoneNumber, 2, TimeUnit.MINUTES, activity, mCallbacks);
        });

    }


    private Single<AuthResult> signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        return Single.<AuthResult>create(emitter -> {
            auth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser user = task.getResult().getUser();

                    userId = userId.map(map -> user.getUid());
                    emitter.onSuccess(AuthResult.SIGN_IN_SUCCESSFUL);
                    Timber.d("onComplete() called with: task = [" + task + "]");

                } else {
                    // Sign in failed, display a message and update the UI
                    Timber.d(task.getException(), "signInWithCredential:failure");
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        emitter.onSuccess(AuthResult.ERR_INVALID_CREDENTIALS);
                    }

                    if (task.getException() instanceof FirebaseTooManyRequestsException) {
                        emitter.onSuccess(AuthResult.TO_MANY_LOGIN_REQUEST);
                    }

                    if (task.getException() instanceof FirebaseNetworkException) {
                        emitter.onSuccess(AuthResult.ERR_NETWORK);
                    } else emitter.onSuccess(AuthResult.UNKNOWN);


                }
            });
        });

    }

    public Single<String> signInWithEmailAndPassword(final String email, final String password) {


        return Single.<String>create(emitter -> {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Timber.d("signInWithEmail:success");
                                final FirebaseUser user = task.getResult().getUser();
                                emitter.onSuccess(user.getUid());
                            } else {
                                // If sign in fails, display a message to the user.
                                emitter.onError(task.getException());

                            }

                            // ...
                        }
                    });
        });
    }

    public Single<String> createUserWithEmailAndPassword(final Person person, final String password) {
        return Single.<String>create(emitter -> {
            auth.createUserWithEmailAndPassword(person.getEmail(), password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithEmail:success");
                            final FirebaseUser user = task.getResult().getUser();


                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(person.retrieveFullName())
                                    .build();

                            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Timber.i("User profile created");
                                        return;
                                    }

                                    Timber.i("User profile could not be created");
                                }
                            });


                            emitter.onSuccess(user.getUid());
                        } else {

                            emitter.onError(task.getException());

                        }

                        // ...
                    });
        });

    }


    public Single<AuthResult> verifySmsCode(String code) {
        Timber.d("VerificationId: " + verificationId + " code: " + code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        return signInWithPhoneAuthCredential(credential);
    }

    public void resendCode(String phoneNumber, FragmentActivity activity) {
        PhoneAuthProvider.getInstance(auth).verifyPhoneNumber(phoneNumber, 2, TimeUnit.MINUTES, activity, mCallbacks);
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void signOut() {
        auth.signOut();
    }


    public interface AuthListener {
        void onAction(String id, String phoneNumber, AuthResult error);
    }


    public enum AuthResult {
        None, ERR_NETWORK, ERR_INVALID_CREDENTIALS, UNKNOWN, TO_MANY_LOGIN_REQUEST, CODE_SENT, SUCCESSFUL, SIGN_IN_SUCCESSFUL, USER_CREATED, SIGN_UP_REQUIRED, SIGNED_OUT, SIGNED_IN, USER_EXISTS, USER_DOES_NOT_EXISTS, SIGN_UP, SIGN_IN, ERR_UNKNOWN, PROCCESSIGN, ERROR, USER_UPDATED, USER_RECORD_NOT_FOUND_ON_CLOUD, ERR_REAUTH_REQUIRED, USER_COLLISOION;
        private RequestResult requestResult;
        private String key;

        public RequestResult getRequestResult() {
            return requestResult;
        }

        public void setRequestResult(RequestResult requestResult) {
            this.requestResult = requestResult;
        }

        public String getKey() {
            return key;
        }

        public AuthResult setKey(String key) {
            this.key = key;
            return this;
        }
    }


}
