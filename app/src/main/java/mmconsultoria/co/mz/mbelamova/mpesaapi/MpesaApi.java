package mmconsultoria.co.mz.mbelamova.mpesaapi;


import android.app.Application;

import mmconsultoria.co.mz.mbelamova.R;

public class MpesaApi {
    private Mpesa mpesa;

    private MpesaApi(Application application) {
        mpesa = Mpesa.init(application.getString(R.string.host), application.getString(R.string.provider), application.getString(R.string.auth));
    }

    public static Mpesa init(Application application) {
        return new MpesaApi(application).mpesa;
    }


}
