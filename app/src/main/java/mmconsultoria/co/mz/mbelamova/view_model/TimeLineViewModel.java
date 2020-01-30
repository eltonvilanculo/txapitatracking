package mmconsultoria.co.mz.mbelamova.view_model;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import mmconsultoria.co.mz.mbelamova.cloud.CloudRepository;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;
import mmconsultoria.co.mz.mbelamova.model.Trip;

public class TimeLineViewModel extends AuthModel {
    private MutableLiveData<List<Trip>> liveData;

    public TimeLineViewModel(@NonNull Application application) {
        super(application);
        liveData = new MutableLiveData<>();
        liveData.setValue(new ArrayList<>());

        init();
    }

    private void init() {
        CloudRepository<Trip> repository = new CloudRepository<>(getApplication(), Trip.class);
        List<Trip> list = liveData.getValue();
        repository.attachListListener()
                .subscribe(response -> {
                    if (response.getRequestResult() == RequestResult.SUCCESSFULL && response.getMovement() == CloudRepository.DatabaseMovement.Addition) {
                        list.add(response.getData());
                    }
                }, this::onError);

    }


    public MutableLiveData<List<Trip>> getLiveData() {
        return liveData;
    }
}
