package mmconsultoria.co.mz.mbelamova.activity;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import androidx.core.util.Consumer;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.model.Notification;
import mmconsultoria.co.mz.mbelamova.model.SubData;
import timber.log.Timber;

public class VolleyRequestQueuer {
    private static VolleyRequestQueuer instance;
    private RequestQueue requestQueue;
    private Context ctx;
    private String serverkey;

    private VolleyRequestQueuer(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
        serverkey = context.getString(R.string.server_key);
    }


    public static synchronized VolleyRequestQueuer init(Context context) {
        if (instance == null) {
            instance = new VolleyRequestQueuer(context);
        }
        return instance;
    }


    public Executor sendDataNotification(String title, String subject, SubData data, String to) {
        Notification notification = new Notification();
        notification.setData(data);
        notification.setTitle(title);
        notification.setSubject(subject);
        notification.setTo(to);


        Executor executor = new Executor(notification, this);


        return executor;
    }

    public Executor sendDataNotification(Notification notification) {
        return new Executor(notification, this);
    }


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you fromUser leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }


    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static class Executor {
        private Notification notification;
        private VolleyRequestQueuer queuer;
        private Consumer success;
        private Consumer<Throwable> error;
        private boolean isConsuming = false;
        private Throwable volleyError;
        private Object response;

        public Executor(Notification notification, VolleyRequestQueuer queuer) {
            this.notification = notification;
            this.queuer = queuer;
        }

        public Executor(JSONException e) {
            volleyError = e;
            if (error != null)
                error.accept(e);
        }


        public Executor consume() {
            isConsuming = true;
            return this;
        }

        public Executor fire() {
            isConsuming = false;
            if (response != null)
                success.accept(response);
            if (error != null)
                error.accept(volleyError);

            return this;
        }

        public Executor execute(Consumer success, Consumer<Throwable> error) {
            this.success = success;
            this.error = error;


            Map<String, String> params = new HashMap<>();
            params.put("Authorization", "key=" + queuer.serverkey);
            params.put("Content-Type", "application/json");



                /*JsonObject object = new JsonObject();

                object.addProperty("to", notification.getTo());

                object.addProperty("subtitle", notification.getSubject());

                JsonObject data = new JsonObject();
                data.addProperty("porp", notification.getData().getPorpouse());
                data.addProperty("message", notification.getData().getMessage());

                object.addPassenger("data", data);


*/

            MoshiRequest<Notification> jsonObjectRequest = new MoshiRequest<Notification>("https://fcm.googleapis.com/fcm/send", notification, Notification.class, params,
                    jsonResponse -> {
                        response = jsonResponse;
                        Timber.i("response: %s", jsonResponse);
                        if (!isConsuming)
                            success.accept(jsonResponse);

                    },

                    jsonError -> {
                        volleyError = jsonError;
                        Timber.i(jsonError.getMessage());
                        if (!isConsuming)
                            error.accept(jsonError);

                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return params;
                }
            };

            queuer.addToRequestQueue(jsonObjectRequest);


            return this;
        }

    }

}
