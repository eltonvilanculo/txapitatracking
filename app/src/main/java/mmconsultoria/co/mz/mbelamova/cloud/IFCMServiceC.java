package mmconsultoria.co.mz.mbelamova.cloud;

import mmconsultoria.co.mz.mbelamova.model.FCMResponseC;
import mmconsultoria.co.mz.mbelamova.model.SenderC;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMServiceC {
        @Headers(
                {
                        "Content-Type:aplication/json",
                        "Authorization:key=AAAApUDusxQ:APA91bHwQixgvKqHUOjDf5-NKuEL3UCwH5opanWh6k5bWT1P1WJXEEff_sVlyQbeXGyEmDZzEbB8SCNGG8URQajocVeU7jT0lZVX-55BJ4l6TsPQjUNUS1G7fK8MNssKvJmKQRWJbr47"

                }
        )
    @POST("fcm/send")
    Call<FCMResponseC> sendMessage(@Body SenderC body);

}
