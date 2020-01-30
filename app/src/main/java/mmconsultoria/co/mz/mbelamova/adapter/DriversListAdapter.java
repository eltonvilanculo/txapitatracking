package mmconsultoria.co.mz.mbelamova.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.model.AvailableRide;
import mmconsultoria.co.mz.mbelamova.util.WordValidator;
import timber.log.Timber;


public class DriversListAdapter extends RecyclerView.Adapter<DriversListAdapter.DLViewHolder> {

    Context context;
    ArrayList<AvailableRide> listaMoto;


    private OnDriverClick onDriverClick;


    public DriversListAdapter(Context c, ArrayList<AvailableRide> p) {
        context = c;
        listaMoto = p;
    }

    @NonNull
    @Override
    public DLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DLViewHolder(LayoutInflater.from(context).inflate(R.layout.driver_list_layout_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DLViewHolder holder, int position) {
        AvailableRide ride = listaMoto.get(position);
        holder.nomeMoto.setText(ride.getRide().getDriverName());
        holder.price.setText(WordValidator.validateLongMoney(ride.getPrice()));
        holder.rate.setRating(ride.getRide().getDriverRating());

        if (!TextUtils.isEmpty(ride.getRide().getDriverPhoto())) {
            Picasso.with(context).load(ride.getRide().getDriverPhoto()).centerInside().fit().into(holder.imagemMoto);
        }


        holder.itemView.setOnClickListener(v -> {
            Timber.d("Setonclick");
            if (onDriverClick != null) {
                onDriverClick.handle(ride);
                Timber.d("deployed value to the listener");
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaMoto.size();
    }

    public List<AvailableRide> getList() {
        return listaMoto;
    }

    public void setOnDriverClick(OnDriverClick onDriverClick) {
        this.onDriverClick = onDriverClick;
    }

    public void clear() {
        for (int i = 0; i < listaMoto.size(); i++) {
            listaMoto.remove(i);

        }
    }

    public void add(AvailableRide p) {
        listaMoto.add(p);
    }

    class DLViewHolder extends RecyclerView.ViewHolder {

        private final TextView price;
        private final RatingBar rate;
        de.hdodenhof.circleimageview.CircleImageView imagemMoto;
        TextView nomeMoto;

        public DLViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemMoto = itemView.findViewById(R.id.drivers_list_image);
            nomeMoto = itemView.findViewById(R.id.drivers_list_name);
            price = itemView.findViewById(R.id.drivers_list_price);
            rate = itemView.findViewById(R.id.drivers_list_rating);
        }


    }

    public interface OnDriverClick {
        void handle(AvailableRide person);
    }
}
