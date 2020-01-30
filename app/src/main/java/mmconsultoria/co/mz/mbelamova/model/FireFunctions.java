package mmconsultoria.co.mz.mbelamova.model;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.fragment.Response;
import timber.log.Timber;

public class FireFunctions {
    private static FireFunctions instance;


    private final FirebaseFunctions mFunctions;

    public static FireFunctions init() {

        if (instance == null) {
            instance = new FireFunctions();
        }

        return instance;
    }

    private FireFunctions() {
        mFunctions = FirebaseFunctions.getInstance();
    }


    public LiveData<Response<List<AvailableRide>>> searchDrivers(Trip trip) {
        Place startPoint = trip.startPoint();
        Place endPoint = trip.endPoint();

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("startLat", startPoint.getLatitude());
        data.put("startLon", startPoint.getLongitude());
        data.put("endLat", endPoint.getLatitude());
        data.put("endLon", endPoint.getLongitude());
        data.put("scheduleDate", trip.getStartTime());
        data.put("push", true);

        MutableLiveData<Response<List<AvailableRide>>> rides = new MutableLiveData<>();


        mFunctions
                .getHttpsCallable("searchDrivers")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {


                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = null;
                        Response<List<AvailableRide>> response = new Response<>();
                        response.setNextToken(null);
                        response.setKey(null);

                        try {
                            Timber.d("Result: %s", task.isSuccessful());
                            Timber.d("Result: %s", task.getResult().getData());


                            result = task.getResult().getData().toString();

                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray arrays = jsonObject.getJSONArray("rides");
                            Timber.d("List of Rides: %s, size: %s", arrays, arrays.length());
                            List<AvailableRide> listOfRides = new ArrayList<>();

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AvailableRide>>() {
                            }.getType();

                            listOfRides = gson.fromJson(arrays.toString(), type);

                            for (AvailableRide ride : listOfRides) {
                                Timber.i("Ride: %s", ride);
                            }

                            response.setData(listOfRides);
                        } catch (NullPointerException e) {
                            Timber.e(e);
                            response.setRequestResult(RequestResult.ERR_UNKNOWN);
                        } finally {
                            rides.postValue(response);
                        }
                        return result;
                    }
                });

        return rides;
    }

    public LiveData<Response<FireRequestResponse>> makeDeposit(Person person, double amount, FireRequestResponse.Type paymentType) {
        LiveData<Response<FireRequestResponse>> liveData = new MutableLiveData<>();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", person.getId());
        data.put("phoneNumber", person.getPhoneNumber());
        data.put("amount", amount);
        data.put("paymentType", paymentType.name());
        data.put("push", true);

        Timber.d("makeDeposit");

        mFunctions
                .getHttpsCallable("makeDeposit")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {


                        Response<FireRequestResponse> response = new Response<>();
                        String result = "";
                        if (!task.isSuccessful()) {
                            response.setRequestResult(RequestResult.ERR_OPERATION_CANCELED);
                            return result;
                        }


                        try {
                            Timber.d("Result: %s", task.isSuccessful());

                            FireRequestResponse requestResponse = getServerResponse(task.getResult().getData().toString());
                            response.setData(requestResponse);
                            ((MutableLiveData<Response<FireRequestResponse>>) liveData).postValue(response);

                        } catch (NullPointerException e) {
                            Timber.e(e);
                            response.setRequestResult(RequestResult.ERR_UNKNOWN);
                        } finally {
                            ((MutableLiveData<Response<FireRequestResponse>>) liveData).postValue(response);
                        }
                        return result;
                    }
                });

        return liveData;
    }

    public LiveData<Response<FireRequestResponse>> redrawMoney(double amount, FireRequestResponse.Type paymentType) {
        LiveData<Response<FireRequestResponse>> liveData = new MutableLiveData<>();
        Map<String, Object> data = new HashMap<>();
        data.put("amount", amount);
        data.put("type", paymentType.name());
        data.put("push", true);

        mFunctions
                .getHttpsCallable("redrawMoney")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {


                        Response<FireRequestResponse> response = new Response<>();
                        String result = "";
                        if (!task.isSuccessful()) {
                            response.setRequestResult(RequestResult.ERR_OPERATION_CANCELED);
                            return result;
                        }


                        try {
                            Timber.d("Result: %s", task.isSuccessful());

                            FireRequestResponse requestResponse = getServerResponse(task.getResult().getData().toString());
                            response.setData(requestResponse);
                            ((MutableLiveData<Response<FireRequestResponse>>) liveData).postValue(response);

                        } catch (NullPointerException e) {
                            Timber.e(e);
                            response.setRequestResult(RequestResult.ERR_UNKNOWN);
                        } finally {
                            ((MutableLiveData<Response<FireRequestResponse>>) liveData).postValue(response);
                        }
                        return result;
                    }
                });

        return liveData;
    }

    public LiveData<Response<FireRequestResponse>> payRide(Person person, String rideId, Trip trip) {
        // Create the arguments to the callable function.
        LiveData<Response<FireRequestResponse>> liveData = new MutableLiveData<>();
        Map<String, Object> data = new HashMap<>();
        data.put("rideId", rideId);
        data.put("phoneNumber", person.getPhoneNumber());
        data.put("amount", trip.getPrice());
        data.put("push", true);


        mFunctions
                .getHttpsCallable("payRide")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Response<FireRequestResponse> response = new Response<>();
                        String result = "";
                        if (!task.isSuccessful()) {
                            response.setRequestResult(RequestResult.ERR_OPERATION_CANCELED);
                            return result;
                        }


                        try {
                            Timber.d("Result: %s", task.isSuccessful());

                            FireRequestResponse requestResponse = getServerResponse(task.getResult().getData().toString());
                            response.setData(requestResponse);
                            ((MutableLiveData<Response<FireRequestResponse>>) liveData).postValue(response);

                        } catch (NullPointerException e) {
                            Timber.e(e);
                            response.setRequestResult(RequestResult.ERR_UNKNOWN);
                        } finally {
                            ((MutableLiveData<Response<FireRequestResponse>>) liveData).postValue(response);
                        }
                        return result;
                    }
                });

        return liveData;
    }

    private FireRequestResponse getServerResponse(String json) {
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

        return gson.fromJson(json, FireRequestResponse.class);
    }

    public LiveData<Response<FireRequestResponse>> confirmTripClient(String rideId) {
        // Create the arguments to the callable function.
        LiveData<Response<FireRequestResponse>> liveData = new MutableLiveData<>();
        Map<String, Object> data = new HashMap<>();
        data.put("rideId", rideId);
        data.put("push", true);

        mFunctions
                .getHttpsCallable("confirmTripClient")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {


                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = null;
                        Response<FireRequestResponse> response = new Response<>();
                        response.setNextToken(null);
                        response.setKey(null);

                        try {
                            Timber.d("Result: %s", task.getResult().getData());

                            result = task.getResult().getData().toString();

                            JSONObject jsonObject = new JSONObject(result);
                            Timber.d("JSON: %s", task.getResult().getData());

                            Type type = new TypeToken<FireRequestResponse>() {
                            }.getType();

                            FireRequestResponse fireRequestResponse = new Gson().fromJson(jsonObject.toString(), type);

                            Timber.d("response: %s", fireRequestResponse);
                            response.setRequestResult(RequestResult.SUCCESSFULL);

                            response.setData(fireRequestResponse);

                        } catch (NullPointerException e) {
                            Timber.e(e);
                            response.setRequestResult(RequestResult.ERR_UNKNOWN);
                        } finally {
                            ((MutableLiveData<Response<FireRequestResponse>>) liveData).postValue(response);
                        }
                        return result;
                    }
                });

        return liveData;
    }

    private String getMpesaResponse(String code) {
        String message = "Erro desconhecido";
        int respCode;

        //INTERNAL ERROR
        if (code.equals("INS-5")) {
            respCode = 401;
            message = "Transacao cancelada";
        }

        if (code.equals("INS-6")) {
            respCode = 401;
        }

        //CANCELED BY CUSTUMER
        if (code.equals("INS-5")) {
            respCode = 401;
            message = "Transacao cancelada pelo Usuario";
        }

        //TIMEOUT
        if (code.equals("INS-9")) {
            respCode = 408;
            message = "Time out";
        }

        //INVALID AMOUNT
        if (code.equals("INS-15")) {
            respCode = 402;
            message = "Quantia invalida";


        }

        //TO MANY REQUESTS ON SERVER
        if (code.equals("INS-16")) {
            respCode = 503;
            message = "Demasiados pedidos ao servidor";


        }

        //INVALID STATS, CONTACT MPESA
        if (code.equals("INS-23")) {
            respCode = 400;
            message = "Erro desconhecido, contacte o Mpesa";


        }

        //ACCOUNT NOT ACTIVE
        if (code.equals("INS-996")) {
            respCode = 400;
            message = "Conta nao Activa";


        }

        //RECEIVER INVALID
        if (code.equals("INS-2002")) {
            respCode = 400;
            message = "Receptor invalido";


        }

        //INSUFICIENT BALANCE
        if (code.equals("INS-2006")) {
            respCode = 422;
            message = "Saldo insuficiente";

        }

        //MSISDN INVALID
        if (code.equals("INS-2051")) {
            respCode = 400;
            message = "Numero  Invalido";

        }
        return message;

    }


}
