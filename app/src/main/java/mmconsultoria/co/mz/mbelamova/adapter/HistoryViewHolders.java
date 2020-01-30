package mmconsultoria.co.mz.mbelamova.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import mmconsultoria.co.mz.mbelamova.R;

/**
 * Created by manel on 10/10/2017.
 */

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView rideId;
    public HistoryViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        rideId = (TextView) itemView.findViewById(R.id.rideId);
    }


    @Override
    public void onClick(View v) {
    }
}
