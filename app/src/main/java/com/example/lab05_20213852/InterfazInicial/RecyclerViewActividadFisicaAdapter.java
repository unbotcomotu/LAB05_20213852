package com.example.lab05_20213852.InterfazInicial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab05_20213852.Objetos.ActividadFisica;
import com.example.lab05_20213852.Objetos.Comida;
import com.example.lab05_20213852.R;

import java.util.List;

public class RecyclerViewActividadFisicaAdapter extends RecyclerView.Adapter<RecyclerViewActividadFisicaAdapter.ViewHolder>{
    private List<ActividadFisica> actividadesFisicas;

    public RecyclerViewActividadFisicaAdapter(List<ActividadFisica> actividadesFisicas) {
        this.actividadesFisicas = actividadesFisicas;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_elemento_actividadfisica, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActividadFisica actividadFisica = actividadesFisicas.get(position);
        holder.nombreActividad.setText(actividadFisica.getNombre());
        holder.gastoCalorico.setText(actividadFisica.getGastoCalorico()+" cal");
        holder.tiempo.setText(actividadFisica.getTiempo());
    }

    @Override
    public int getItemCount() {
        return actividadesFisicas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreActividad;
        TextView gastoCalorico;
        TextView tiempo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreActividad = itemView.findViewById(R.id.nombreActividad);
            gastoCalorico = itemView.findViewById(R.id.gastoCalorico);
            tiempo=itemView.findViewById(R.id.tiempo);
        }
    }
}
