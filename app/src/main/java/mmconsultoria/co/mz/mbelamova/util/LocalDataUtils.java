package mmconsultoria.co.mz.mbelamova.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mmconsultoria.co.mz.mbelamova.cloud.DatabaseValue;
import mmconsultoria.co.mz.mbelamova.model.Controller;
import mmconsultoria.co.mz.mbelamova.model.DriverLicence;
import mmconsultoria.co.mz.mbelamova.model.Person;
import mmconsultoria.co.mz.mbelamova.model.Ride;
import timber.log.Timber;

public class LocalDataUtils {
    private Context context;

    public LocalDataUtils(Context application) {
        context = application;
    }

    public static LocalDataUtils from(Application application) {
        return new LocalDataUtils(application);
    }

    public  <T> void saveObject(String name, T data) {
        Class aclass = data.getClass();

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        String json = gson.toJson(data);

        SharedPreferences preferences = context.getSharedPreferences(aclass.getName() + "." + name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("data", json);
        editor.apply();
        Timber.d("saved data: %s", json);



    }

    public  <T> T getObject(String name, Class<T> aClass) {


        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls()
                .create();


        SharedPreferences preferences = context.getSharedPreferences(aClass.getName() + "." + name, Context.MODE_PRIVATE);
        String json = preferences.getString("data", "");

        T fromJson = gson.fromJson(json, aClass);
        Timber.d("Found user data: %s", fromJson);
        return fromJson;


    }

    public Person loadPerson() {
        SharedPreferences preferences = context.getSharedPreferences(DatabaseValue.AuthData.name(), Context.MODE_PRIVATE);
        Person person = new Person();

        String firstName = preferences.getString(DatabaseValue.FIRST_NAME.name(), "");
        String lastName = preferences.getString(DatabaseValue.FAMILY_NAME.name(), "");
        String phoneNumber = preferences.getString(DatabaseValue.PHONE_NUMBER.name(), "");
        String userId = preferences.getString(DatabaseValue.USER_ID.name(), "");
        String notifyId = preferences.getString(DatabaseValue.NOTIFICATION.name(), "");
        String photoUri = preferences.getString(DatabaseValue.PHOTO_URI.name(), "");
        String email = preferences.getString(DatabaseValue.EMAIL.name(), "");

        person.setName(firstName);
        person.setLastName(lastName);
        person.setPhoneNumber(phoneNumber);
        person.setEmail(email);
        person.setId(userId);
        person.setNotificationId(notifyId);
        person.setPhotoUri(photoUri);

        Timber.d("Found user data fromUser local %s", person);

        //TODO transferindo person
        Controller.getInstance().setPerson(person);
        return validate(person);
    }

    public void putPerson(Person person) {
        if (person != null) {
            SharedPreferences preferences = context.getSharedPreferences(DatabaseValue.AuthData.name(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(DatabaseValue.FIRST_NAME.name(), person.getName());
            editor.putString(DatabaseValue.FAMILY_NAME.name(), person.getLastName());
            editor.putString(DatabaseValue.EMAIL.name(), person.getEmail());
            editor.putString(DatabaseValue.PHONE_NUMBER.name(), person.getPhoneNumber());
            editor.putString(DatabaseValue.NOTIFICATION.name(), person.getNotificationId());
            editor.putString(DatabaseValue.USER_ID.name(), person.getId());
            editor.putString(DatabaseValue.PHOTO_URI.name(), person.getPhotoUri());
            editor.apply();

            Timber.i("Successfully inserted person: %s", person);
        }
    }

    private Person validate(Person person) {
        if (person.getId().trim().isEmpty())
            return null;
        return person;
    }

    public void saveLastRideData(Ride ride) {
        if (ride != null) {
            SharedPreferences preferences = context.getSharedPreferences(DatabaseValue.RIDE_DATA.name(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(DatabaseValue.RIDE_ID.name(), ride.getId());
            /*editor.putString(DatabaseValue.RIDE_START_LAT.name(), ride.getStartPoint().getLatitude() + "");
            editor.putString(DatabaseValue.RIDE_START_LONG.name(), ride.getStartPoint().getLongitude() + "");
            editor.putString(DatabaseValue.RIDE_END_LAT.name(), ride.getEndPoint().getLatitude() + "");
            editor.putString(DatabaseValue.RIDE_END_LONG.name(), ride.getEndPoint().getLongitude() + "");
*/
//            editor.putString(DatabaseValue.DRIVER_LICENCE_TYPE.name(), ride.getDriverLicence().getType());
            editor.apply();
            Timber.i("Successfully inserted ride: %s ", ride);
        }

    }

    public Ride getLastRideData() {

        SharedPreferences preferences = context.getSharedPreferences(DatabaseValue.RIDE_DATA.name(), Context.MODE_PRIVATE);

        String value = DatabaseValue.NULL.name();

        try {
            Ride ride = new Ride();

            String rideId = preferences.getString(DatabaseValue.FIRST_NAME.name(), value);
            String startlat = preferences.getString(DatabaseValue.FAMILY_NAME.name(), value);
            String startLong = preferences.getString(DatabaseValue.EMAIL.name(), value);
            String endlat = preferences.getString(DatabaseValue.PHONE_NUMBER.name(), value);
            String endLong = preferences.getString(DatabaseValue.NOTIFICATION.name(), value);

            ride.setId(rideId);

            DriverLicence driverLicence = new DriverLicence();

            ride.setDriverLicence(driverLicence);
            /*ride.setStartPoint(Place.build(parseDouble(startlat), parseDouble(startLong)).build());
            ride.setEndPoint(Place.build(parseDouble(endlat), parseDouble(endLong)).build());
*/
            Timber.i("loaded ride: %s", ride);

            return ride;
        } catch (IllegalArgumentException | NullPointerException exception) {
            Timber.i(exception, "last ride not found");
            return null;
        }


    }
}
