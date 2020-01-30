package mmconsultoria.co.mz.mbelamova.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.model.History;

public class HistoryAdapterByGam extends RecyclerView.Adapter<HistoryAdapterByGam.ViewHolder> {

    private List<History> listModelo;
    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;


    public HistoryAdapterByGam(Context context, List<History> listModelo) {
        inflater = LayoutInflater.from(context);
        this.listModelo = listModelo;

    }

    @NonNull
    @Override


    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.finished_trips_list_layout_card, parent, false);
        view.setOnClickListener(onClickListener);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = listModelo.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Timber.d(driverLicenceActual.toString());
            }
        });
        holder.setData(history, position);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return listModelo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       // de.hdodenhof.circleimageview.CircleImageView imageView;
        TextView idViagem;
        TextView localPartida;
        TextView localDestino;
        TextView nomeMotorista;
        TextView dataViagem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //imageView = itemView.findViewById(R.id.drivers_list_image);
            nomeMotorista = itemView.findViewById(R.id.nomeMotorista);
            idViagem=itemView.findViewById(R.id.id_viagem);
            localPartida=itemView.findViewById(R.id.localPartida);
            localDestino=itemView.findViewById(R.id.localDestino);
            dataViagem=itemView.findViewById(R.id.data_viagem);


        }

        public void setData(History history, int position) {
            history.setNomeMoto("Gimo Mhula");
          // this.imageView.setImageResource(driverLicenceActual.getImageSrc());

            this.nomeMotorista.setText(history.getNomeMoto());



        }
    }

    @FunctionalInterface
    public interface OnClickListener {
        void handle(History history);
    }
}
