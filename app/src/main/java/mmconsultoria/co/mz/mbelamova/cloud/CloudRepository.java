package mmconsultoria.co.mz.mbelamova.cloud;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mmconsultoria.co.mz.mbelamova.fragment.Response;
import mmconsultoria.co.mz.mbelamova.model.MultipleMediaSource;
import mmconsultoria.co.mz.mbelamova.model.Place;
import timber.log.Timber;

import static java.lang.String.valueOf;

public class CloudRepository<T> {
    private DatabaseReference dataRef;
    private Application application;
    private Class<T> classType;
    private String root;
    private int i;


    public CloudRepository(Application application, Class<T> classType) {
        this.application = application;
        this.classType = classType;
        root = classType.getSimpleName();

        dataRef = FirebaseDatabase.getInstance().getReference();

setPath(root);

    }

    public Single<Response<String>> setData(@NonNull T data) {
        return setData(data, "");
    }

    public Single<Response<String>> setData(@Nullable Uri uri, @NonNull T data) {
        List<String> thisUri = new ArrayList<>();
        thisUri.add(uri.toString());
        return setData(data, thisUri);
    }

    public Single<Response<String>> setData(@NonNull T data, List<String> imageUris) {
        return upload(data, imageUris, false, "");
    }

    public Single<Response<String>> setData(@NonNull T data, @Nullable String name) {
        return upload(data, null, false, name);
    }

    public Single<Response<String>> setChildData(@Nullable String uri, @NonNull T data, @Nullable String name) {
        List<String> thisUri = new ArrayList<>();
        thisUri.add(uri);
        return setChildData(data, thisUri, name);
    }

    public Single<Response<String>> setChildData(@NonNull T data, List<String> imageUris, @Nullable String name) {
        return upload(data, imageUris, true, name);
    }

    public Single<Response<String>> setChildData(@NonNull T data, @Nullable String name) {
        return setChildData(data, null, name);
    }

    public Single<Response<String>> setChildData(@NonNull T data) {
        return setChildData(data, null, null);
    }


    private Single<Response<String>> upload(@NonNull T data, List<String> imageUris, boolean isChild, @Nullable String name) {
        Timber.d("images: %s, data name: %s", valueOf(imageUris), name);

        DatabaseReference thisRef;
        if (isChild) {
            if (name == null || name.isEmpty())
                thisRef = dataRef.push();
            else
                thisRef = dataRef.child(name);
        } else thisRef = dataRef;

        if (data == null) {
            throw new NullPointerException("data field cannot be null!");
        }

        return Single.create(emitter -> {

            if (imageUris == null || imageUris.isEmpty())

                upload(thisRef, data)
                        .observeOn(Schedulers.io())
                        .subscribe(emitter::onSuccess, emitter::onError);

            else {
                for (i = 0; i < imageUris.size(); i++) {
                    String uri = imageUris.get(i);
                    if (uri != null && !uri.isEmpty())
                        uploadImage(thisRef.getKey(), Uri.parse(uri))
                                .observeOn(Schedulers.io()).subscribe(dataUri -> {
                            Timber.d("ImageUri: %s", dataUri);
                            if (data instanceof ImageHolder) {
                                ImageHolder imageHolder = (ImageHolder) data;
                                imageHolder.setPhotoUri(dataUri.toString());
                                Timber.d("instance of ImageHolder %s", data);
                            }

                            if (data instanceof MultipleMediaSource) {
                                MultipleMediaSource source = (MultipleMediaSource) data;
                                source.getDataUris().add(dataUri.toString());
                                Timber.d("instance of MultipleMediaSource %s", data);
                            }

                            if (i == imageUris.size() - 1) {
                                upload(thisRef, data)
                                        .observeOn(Schedulers.io())
                                        .subscribe(emitter::onSuccess, emitter::onError);
                            }

                        }, throwable -> {
                            Timber.d(throwable);
                            if (i == imageUris.size() - 1) {
                                upload(thisRef, data)
                                        .observeOn(Schedulers.io())
                                        .subscribe(emitter::onSuccess, emitter::onError);
                            }

                        });

                    if (i == imageUris.size() - 1) {
                        upload(thisRef, data)
                                .observeOn(Schedulers.io())
                                .subscribe(emitter::onSuccess, emitter::onError);
                    }
                }

            }


        });

    }

    private Single<Uri> uploadImage(String filename, @NonNull Uri uri) {
        Timber.d(root);
        Timber.d(uri.toString());


        StorageReference reference = FirebaseStorage.getInstance()
                .getReference(root)
                .child(filename +
                        System.currentTimeMillis() +
                        "." +
                        getFileExtension(uri));


        return Single.create(emitter -> {
            if (uri == null || uri.toString().isEmpty()) {
                emitter.onError(new NullPointerException("Uri is null or empty!"));
                return;
            }

            reference.putFile(uri).continueWithTask(
                    task -> {
                        if (!task.isSuccessful()) {
                            Timber.d(valueOf(task));
                            emitter.onError(task.getException());
                        }

                        // Continue with the task to get the download URL

                        return reference.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Timber.d(valueOf(downloadUri));
                    emitter.onSuccess(downloadUri);
                } else {
                    emitter.onSuccess(Uri.EMPTY);
                }
            }).addOnFailureListener(emitter::onError);
        });

    }

    private Observable<Response<String>> upload(DatabaseReference dataRef, @NonNull T data) {

        return Observable.create(emitter -> {
            dataRef.setValue(data).addOnSuccessListener(success -> {
                emitter.onNext(new Response<>(dataRef.getKey(), RequestResult.SUCCESSFULL, DatabaseMovement.Upload));
            }).addOnFailureListener(emitter::onError);
        });

    }

    public Single<Response<T>> removeChildFromCloud(String id, final OnChildValueListener<T> onRemoveValue) {
        return Observable.<Response<T>>create(emitter -> {
            dataRef.child(id).removeValue(((databaseError, databaseReference) -> {
                if (onRemoveValue == null) {
                    return;
                }

                if (hasError(emitter, databaseError, DatabaseMovement.Removal)) {
                    return;
                }

                onRemoveValue.onMovement(new Response<>(null, RequestResult.SUCCESSFULL, DatabaseMovement.Removal));
            }));
        }).singleOrError();

    }

    private boolean hasError(final ObservableEmitter<Response<T>> emitter, DatabaseError databaseError, DatabaseMovement movement) {

        if (databaseError.getCode() == DatabaseError.NETWORK_ERROR) {
            emitter.onNext(new Response<>(null, RequestResult.ERR_NETWORK, movement));
            return true;
        }

        if (databaseError.getCode() == DatabaseError.DISCONNECTED) {
            emitter.onNext(new Response<>(null, RequestResult.ERR_NETWORK, movement));
            return true;
        }

        if (databaseError.getCode() == DatabaseError.UNKNOWN_ERROR) {
            emitter.onNext(new Response<>(null, RequestResult.ERR_UNKNOWN, movement));
            return true;
        }

        if (databaseError.getCode() == DatabaseError.WRITE_CANCELED) {
            emitter.onNext(new Response<>(null, RequestResult.ERR_OPERATION_CANCELED, movement));
            return true;
        }

        return false;
    }


    public Observable<Response<T>> attachListListener() {

        return Observable.create(emitter -> {
            ChildEventListener listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Timber.d("key: %s,received data %s, classType: %s", s, dataSnapshot.getValue(), classType);

                    try {
                        T value = dataSnapshot.getValue(classType);
                        if (value != null) {
                            String key = dataSnapshot.getKey();
                            Response<T> value1 = new Response<>(value, RequestResult.SUCCESSFULL, DatabaseMovement.Addition)
                                    .setKey(key)
                                    .setNextToken(s);

                            emitter.onNext(value1);

                        } else emitter.onError(new NullPointerException());

                    } catch (com.google.firebase.database.DatabaseException | NullPointerException e) {
                        emitter.onError(e);
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Timber.d("key: %s,received data %s, classType: %s", s, dataSnapshot.getValue(), classType);
                    T value = dataSnapshot.getValue(classType);
                    try {
                        if (value != null) {
                            if (!value.getClass().equals(classType)) {
                                emitter.tryOnError(new ClassCastException());
                                return;
                            }

                            emitter.onNext(new Response<>(value, RequestResult.SUCCESSFULL, DatabaseMovement.Update)
                                    .setKey(s));

                        } else emitter.onError(new NullPointerException());

                    } catch (com.google.firebase.database.DatabaseException | NullPointerException e) {
                        emitter.onError(e);
                    }


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Timber.d("received data %s, classType: %s", dataSnapshot.getValue(), classType);
                    try {
                        T value = dataSnapshot.getValue(classType);
                        if (value != null) {
                            if (!value.getClass().equals(classType)) {
                                emitter.tryOnError(new ClassCastException());
                                return;
                            }


                            emitter.onNext(new Response<>(value, RequestResult.SUCCESSFULL, DatabaseMovement.Removal)
                                    .setKey(dataSnapshot.getKey()));
                        } else emitter.onError(new NullPointerException());

                    } catch (com.google.firebase.database.DatabaseException | NullPointerException e) {
                        emitter.onError(e);
                    }


                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Timber.d("received data %s", dataSnapshot.getValue());

                    try {
                        T value = dataSnapshot.getValue(classType);
                        if (value != null) {

                            if (!value.getClass().equals(classType)) {
                                emitter.tryOnError(new ClassCastException());
                                return;
                            }

                            emitter.onNext(new Response<>(value, RequestResult.SUCCESSFULL, DatabaseMovement.Addition)
                                    .setKey(dataSnapshot.getKey())
                                    .setNextToken(s));
                        } else emitter.onError(new NullPointerException());


                    } catch (com.google.firebase.database.DatabaseException | NullPointerException e) {
                        emitter.onError(e);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (hasError(emitter, databaseError, DatabaseMovement.Canceled)) return;
                    emitter.onNext(new Response<>(null, RequestResult.ERR_UNKNOWN.setError(databaseError.toException()), DatabaseMovement.Canceled));
                }
            };
            dataRef.addChildEventListener(listener);
            emitter.setDisposable(new Disposable() {
                @Override
                public void dispose() {
                    dataRef.removeEventListener(listener);
                }

                @Override
                public boolean isDisposed() {
                    return false;
                }
            });
        });


    }

    public Observable<Response<T>> attachListener() {

        return Observable.create(emitter -> {


            ValueEventListener valueEventListener = dataRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Timber.i("received data %s", dataSnapshot.getValue());

                    try {
                        T value = dataSnapshot.getValue(classType);
                        if (value != null) {
                            String key = dataSnapshot.getKey();
                            Timber.i("DataKey: %s", key);


                            Response<T> response = new Response<>(value, RequestResult.SUCCESSFULL, DatabaseMovement.Addition)
                                    .setKey(key);

                            emitter.onNext(response);

                        } else emitter.onError(new NullPointerException());

                    } catch (DatabaseException | NullPointerException e) {
                        emitter.onError(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (hasError(emitter, databaseError, DatabaseMovement.Canceled)) return;

                    emitter.onNext(new Response<>(null, RequestResult.ERR_UNKNOWN, DatabaseMovement.Canceled));
                }
            });

            emitter.setDisposable(new Disposable() {
                @Override
                public void dispose() {
                    dataRef.removeEventListener(valueEventListener);
                }

                @Override
                public boolean isDisposed() {
                    return false;
                }
            });

        });

    }

    public String getFileExtension(Uri path) {
        ContentResolver resolver = application.getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(path));
    }


    public CloudRepository<T> setPath(String path) {
        dataRef = FirebaseDatabase.getInstance().getReference(path);
        if (path.equals("Person") || path.equals("History") || path.equals("Review")){
           dataRef.keepSynced(true);
        }
        return this;
    }


    public CloudRepository<T> setSubPath(CharSequence subPath) {
        dataRef = dataRef.child(subPath.toString());
        return this;
    }

    public Single<Response> updateField(String fieldName, Object value) {
        return Single.create(emitter -> {
            dataRef.updateChildren(Collections.singletonMap(fieldName, value), (databaseError, databaseReference) -> {
                Timber.d("error: %s", databaseError);
                Timber.d("DataRef: %s", databaseReference);


                if (databaseError != null) {
                    emitter.onError(databaseError.toException());
                } else {
                    emitter.onSuccess(new Response<>(null, RequestResult.SUCCESSFULL, DatabaseMovement.Update).setKey(databaseReference.getKey()));
                }
            });
        });
    }

    public Observable<Response<T>> deepEqualsToQuery(List<EntrySet<String>> listOfEntries) {

        return Observable.create(emitter -> {

            Timber.d("ListOfEntries: %s", listOfEntries);
            Query query = subTree(listOfEntries.get(0), dataRef);

           /* for (int i = 1; listOfEntries.size() > i; i++) {
                query = subTree(listOfEntries.get(i), query);
            }*/

            if (query == null) {
                emitter.onError(new NullPointerException("List of entries cannot be null!"));
                return;

            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Timber.i("location: %s ,Raw Data: %s", dataSnapshot.getRef(),dataSnapshot.getValue());
                    try {
                        String key = dataSnapshot.getChildren().iterator().next().getKey();
                        T value = dataSnapshot.child(key).getValue(classType);
                        Timber.i("Data: %s,DataKey: %s", value, key);
                        if (value != null) {

                            emitter.onNext(new Response<>(value, RequestResult.SUCCESSFULL, DatabaseMovement.Update)
                                    .setKey(key));
                        } else emitter.onError(new NullPointerException());

                    } catch (DatabaseException | NullPointerException | NoSuchElementException e) {
                        emitter.onError(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());
                }
            });


        });
    }

    private Query subTree(EntrySet<String> entry, Query query) {
        return query.orderByChild(entry.getKey()).equalTo(entry.getValue());
    }

    private boolean isNotEmpty(String word) {
        return word == null || word.trim().isEmpty();
    }

    public <E> Single<Response<String>> updateChild(String query, E value) {
        return Single.create(emitter -> {
            dataRef.orderByChild(query).getRef().setValue(value, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    emitter.onError(databaseError.toException());
                } else {
                    emitter.onSuccess(new Response<>(databaseReference.getKey(), RequestResult.SUCCESSFULL, DatabaseMovement.Update));
                }
            });
        });
    }

    public enum DatabaseMovement {
        Addition, Update, Moved, Canceled, Upload, Removal, Query
    }

    public interface OnChildValueListener<T> extends CloudListener<T> {
    }

    public interface OnSingleValueListener<T> extends CloudListener<T> {
    }

    public interface CloudListener<T> {
        void onMovement(Response<T> response);

    }

    public Observable<Response<T>> uploadLocation(String key, Place place) {
        return Observable.create(emitter -> {


            new GeoFire(dataRef).setLocation(key, new GeoLocation(place.getLatitude(), place.getLongitude()),
                    (myKey, error) -> {
                        if (error == null) {
                            final Response<T> response = new Response<>();
                            response.setRequestResult(RequestResult.Location_Updated);
                            emitter.onNext(response);
                        } else {
                            emitter.onError(error.toException());
                        }
                    });
        });
    }

    public Observable<Response<Place>> queryLocationByKey(String key) {
        return Observable.create(emitter -> {
            new GeoFire(dataRef).getLocation(key, new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {
                    if (location == null) {
                        Timber.d("There is no location for the given key %s ", key);
                        final Response<Place> response = new Response<>();
                        response.setRequestResult(RequestResult.no_location);
                        emitter.onNext(response);
                    } else {
                        Timber.d("The location for key %s is [%f,%f]", key, location.latitude, location.longitude);
                        final Response<Place> response = new Response<>();
                        response.setRequestResult(RequestResult.Location_Downloaded);
                        response.setData(Place.build(location.latitude, location.longitude).build());
                        emitter.onNext(response);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());


                }
            });
        });
    }

    public Observable<Response<Place>> queryLocation(double latitude, double longitude, double radius) {
        return Observable.create(emitter -> {
            new GeoFire(dataRef).queryAtLocation(new GeoLocation(latitude, longitude), radius).addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Timber.d("The location for key %s is [%f,%f]", key, location.latitude, location.longitude);
                    final Response<Place> response = new Response<>();
                    response.setRequestResult(RequestResult.Location_Downloaded);
                    response.setData(Place.build(location.latitude, location.longitude).build());
                    emitter.onNext(response);
                }

                @Override
                public void onKeyExited(String key) {
                    Timber.d("The location for key exited %s: ", key);
                    final Response<Place> response = new Response<>();
                    response.setRequestResult(RequestResult.Location_key_exited);
                    emitter.onNext(response);
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Timber.d("The location for key %s was moved [%f,%f]", key, location.latitude, location.longitude);
                    final Response<Place> response = new Response<>();
                    response.setRequestResult(RequestResult.Location_key_moved);
                    response.setData(Place.build(location.latitude, location.longitude).build());
                    emitter.onNext(response);
                }

                @Override
                public void onGeoQueryReady() {
                    Timber.d("The location for key was moved ");
                    final Response<Place> response = new Response<>();
                    response.setRequestResult(RequestResult.Location_query_ready);
                    emitter.onNext(response);
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    emitter.onError(error.toException());
                }
            });
        });
    }

    public Observable<Response<Place>> trackLocationChanges(double latitude, double longitude, double radius) {
        return Observable.create(emitter -> {
            new GeoFire(dataRef).queryAtLocation(new GeoLocation(latitude, longitude), radius);
            throw new IllegalArgumentException("not implemented");
        });
    }


}
