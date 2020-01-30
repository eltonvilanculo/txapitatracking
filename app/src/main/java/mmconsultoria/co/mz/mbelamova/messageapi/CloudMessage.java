package mmconsultoria.co.mz.mbelamova.messageapi;

import android.app.Application;


import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import androidx.core.util.Consumer;
import io.reactivex.Observable;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.model.Notification;
import mmconsultoria.co.mz.mbelamova.mpesaapi.RetrofitInterface;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class CloudMessage {

    private final String authorization;

    public CloudMessage(Application application) {
        authorization = "key=" + application.getString(R.string.server_key);
    }

    public static CloudMessage init(Application application) {
        return new CloudMessage(application);
    }

    public Executor send(Notification content) {


        Executor executor = new Executor();
        executor.with(content);
        return executor;
    }

    public static class Content {

        private String authorization;
        @SerializedName("to")
        private String to;
        @SerializedName("text")
        private String message;
        @SerializedName("Note4")
        private String title;


        public void setMessage(String message) {
            this.message = message;
        }

        public void sendTo(String to) {

            this.to = "/topics/" + to;
        }
    }

    public class Executor {
        private Retrofit retrofit;
        private Object result;
        private Throwable throwable;
        private boolean isConsuming = false;
        private Consumer successConsumer;
        private Consumer<Throwable> errorConsumer;
        Observable<Notification> response;
        private Call<Notification> call;

        public void with(Notification content) {
            retrofit = new Retrofit.Builder().baseUrl("https://fcm.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request request = original.newBuilder().addHeader("Content-Type", "application/json")
                                    .addHeader("Authorization", authorization)
                                    .addHeader("Content-type", "application/json")
                                    .method(original.method(), original.body())
                                    .build();

                            return chain.proceed(request);
                        }
                    }).build())
                    .build();


            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

            Timber.d(content.getTo());




            call = retrofitInterface.send(content);
            call.enqueue(new Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> gsonResponse) {
                    result = gsonResponse;

                    Timber.i("HEADERS: %s, body: %s, cache: %s", call.request().headers(), call.request(), call.request().cacheControl().toString());
                    Timber.i("code: %s, message: %s, %s", gsonResponse.code(),gsonResponse.message(), gsonResponse.errorBody());


                    if (successConsumer != null)
                        if (!isConsuming)
                            successConsumer.accept(gsonResponse.raw());
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {
                    throwable = t;
                    if (errorConsumer != null)
                        if (!isConsuming)
                            errorConsumer.accept(t);
                }
            });

        }

        public Executor execute() {
            isConsuming = false;
            return this;
        }

        public Executor then(Consumer consumer) {
            successConsumer = consumer;
            return this;

        }

        public Executor ifError(Consumer<Throwable> consumer) {
            errorConsumer = consumer;
            return this;
        }

        public Executor consume() {
            isConsuming = true;
            return this;
        }

        public void fire() throws Exception {
            isConsuming = false;

            if (result != null) {
                successConsumer.accept(result);
            }

            if (throwable != null) {
                errorConsumer.accept(throwable);
            }

        }

    }
}
