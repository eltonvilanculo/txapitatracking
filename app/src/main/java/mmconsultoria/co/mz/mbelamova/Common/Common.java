package mmconsultoria.co.mz.mbelamova.Common;

import mmconsultoria.co.mz.mbelamova.cloud.FCMClient;
import mmconsultoria.co.mz.mbelamova.cloud.FCMClientC;
import mmconsultoria.co.mz.mbelamova.cloud.IFCMService;
import mmconsultoria.co.mz.mbelamova.cloud.IFCMServiceC;
import mmconsultoria.co.mz.mbelamova.model.IGoogleAPI;
import mmconsultoria.co.mz.mbelamova.model.RetrofitClient;

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static String currenteToken = "";

    public static IGoogleAPI geIGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    public static IFCMServiceC getFCMServiceC(){
        return FCMClientC.getClient(fcmURL).create(IFCMServiceC.class);
    }
}
