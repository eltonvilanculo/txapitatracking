package mmconsultoria.co.mz.mbelamova.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.view_model.MapViewModel;

public class BottomFragment extends BottomSheetDialogFragment {
    private MapViewModel mapModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom, container, false);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mapModel = ViewModelProviders.of(getActivity())
                    .get(MapViewModel.class);
        }
    }
}
