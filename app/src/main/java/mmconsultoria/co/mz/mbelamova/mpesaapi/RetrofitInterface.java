package mmconsultoria.co.mz.mbelamova.mpesaapi;

import io.reactivex.Observable;
import mmconsultoria.co.mz.mbelamova.model.Notification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/ipg/v1/c2bpayment/")
    Observable<RequestPayment> pay(@Body RequestPayment paymentData);

    @POST("/fcm/send")
    Call<Notification> send(@Body Notification dada);
}