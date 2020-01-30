package mmconsultoria.co.mz.mbelamova.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.activity.Location;

public class CustomDialogAdapter extends BaseAdapter {
    private Context context;
    private List<Location> trips;

    public CustomDialogAdapter(Context context, List<Location> trips) {
        this.context = context;
        this.trips = trips;
    }


    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public Object getItem(int position) {
        return trips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.map_trip_dialog_item, parent, false);

        final TextView name = inflate.findViewById(R.id.map_trip_dialog_name);
        final TextView startPoint = inflate.findViewById(R.id.map_trip_dialog_start_point);
        final TextView endPoint = inflate.findViewById(R.id.map_trip_dialog_end_point);


        Location location = trips.get(position);
        startPoint.setText(location.startPoint().getName());
        name.setText(location.name());
        endPoint.setText(location.endPoint().getName());

        return inflate;
    }

}
