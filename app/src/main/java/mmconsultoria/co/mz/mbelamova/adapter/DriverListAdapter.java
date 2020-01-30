package mmconsultoria.co.mz.mbelamova.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.model.DriverLicence;
import timber.log.Timber;

public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.ViewHolder> {

    private List<DriverLicence> listModelo;
    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;


    public DriverListAdapter(Context context, List<DriverLicence> listModelo) {
        inflater = LayoutInflater.from(context);
        this.listModelo = listModelo;

    }

    @NonNull
    @Override


    public DriverListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.driver_list_layout_card, parent, false);
        view.setOnClickListener(onClickListener);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(@NonNull DriverListAdapter.ViewHolder holder, int position) {
        DriverLicence driverLicenceActual = listModelo.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d(driverLicenceActual.toString());
            }
        });
        holder.setData(driverLicenceActual, position);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return listModelo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView imageView;
        TextView txtNome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.drivers_list_image);
            txtNome = itemView.findViewById(R.id.drivers_list_name);
        }

        public void setData(DriverLicence driverLicenceActual, int position) {

          /*  this.imageView.setImageResource(driverLicenceActual.getImageSrc());
            this.txtNome.setText(driverLicenceActual.getPersonName());
*/


        }
    }

    @FunctionalInterface
    public interface OnClickListener {
        void handle(DriverLicence driverLicence);
    }
}
