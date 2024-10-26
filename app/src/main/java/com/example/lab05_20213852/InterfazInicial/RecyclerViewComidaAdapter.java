package com.example.lab05_20213852.InterfazInicial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab05_20213852.Objetos.Comida;
import com.example.lab05_20213852.R;

import java.util.List;

public class RecyclerViewComidaAdapter extends RecyclerView.Adapter<RecyclerViewComidaAdapter.ViewHolder>{
    private List<Comida> desayunos;

    public RecyclerViewComidaAdapter(List<Comida> desayunos) {
        this.desayunos = desayunos;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_elemento_comida, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comida desayuno = desayunos.get(position);
        holder.nombreComida.setText(desayuno.getNombre());
        holder.calorias.setText(desayuno.getCalorias()+" cal");
        holder.tiempo.setText(desayuno.getTiempo());
    }

    @Override
    public int getItemCount() {
        return desayunos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreComida;
        TextView calorias;
        TextView tiempo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreComida = itemView.findViewById(R.id.nombreComida);
            calorias = itemView.findViewById(R.id.calorias);
            tiempo=itemView.findViewById(R.id.tiempo);
        }
    }
}
