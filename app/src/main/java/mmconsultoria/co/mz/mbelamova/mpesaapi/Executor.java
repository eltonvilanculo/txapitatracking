package mmconsultoria.co.mz.mbelamova.mpesaapi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.core.util.Consumer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Executor {
    private Retrofit retrofit;
    private Result result;
    private Throwable throwable;
    private boolean isConsumming = false;
    private Consumer<Result> successConsumer;
    private Consumer<Throwable> errorConsumer;
    Observable<RequestPayment> pay;

    public void with(Mpesa mpesa) {
        retrofit = new Retrofit.Builder().baseUrl("https://api.sandbox.vm.co.mz:18346/")
                .client(new OkHttpClient.Builder().callTimeout(120, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
                        .writeTimeout(100, TimeUnit.SECONDS).addInterceptor(new Interceptor() {

                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();
                                Request request = original.newBuilder().addHeader("Content-Type", "application/json")
                                        .addHeader("Authorization", mpesa.authorization)
                                        .addHeader("Origin", mpesa.origin)
                                        .method(original.method(), original.body())
                                        .build();

                                return chain.proceed(request);
                            }
                        }).build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        RequestPayment request = new RequestPayment();
        request.setTransactionRef(mpesa.transactionReference);
        request.setProviderCode(mpesa.serviceProvider);
        request.setThirdPartRef(mpesa.thirdPartyReference);
        request.setClientSSID(mpesa.phoneNumber);
        request.setBalance(mpesa.value);


        pay = retrofitInterface.pay(request);

    }

    public Executor execute() {
        try {
            pay.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(next -> {
                        System.err.println("success");
                        Result result = new Result();
                        result.setConversationId(next.getResponseConversationId());
                        result.setResultCode(next.getResponseCode());
                        result.setStatus(next.getResponseDescription());
                        result.setTransactionId(next.getResponseTransactionId());

                        if (successConsumer != null) {
                            if (!isConsumming) {
                                successConsumer.accept(result);
                            } else this.result = result;
                        }
                    }, error -> {
                        if (errorConsumer != null)
                            if (!isConsumming)
                                errorConsumer.accept(error);
                            else throwable = error;
                    });
        } catch (Exception exception) {
            System.err.println("catch error");
            if (errorConsumer != null)
                if (!isConsumming) {
                    throwable = exception;
                    try {
                        errorConsumer.accept(throwable);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

        }
        return this;
    }

    public Executor then(Consumer<Result> consumer) {
        successConsumer = consumer;
        return this;

    }

    public Executor ifError(Consumer<Throwable> consumer) {
        errorConsumer = consumer;
        return this;
    }

    public Executor consume() {
        isConsumming = true;
        return this;
    }

    public void fire() throws Exception {
        isConsumming = false;

        if (result != null) {
            successConsumer.accept(result);
        }

        if (throwable != null) {
            errorConsumer.accept(throwable);
        }

    }

}