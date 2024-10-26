package com.example.lab05_20213852.InterfazInicial;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab05_20213852.Objetos.Comida;
import com.example.lab05_20213852.Linkers.DialogDataLink;
import com.example.lab05_20213852.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewComidaFrecuenteAdapter extends RecyclerView.Adapter<RecyclerViewComidaFrecuenteAdapter.ViewHolder>{
    private List<Comida> comidasFrecuentes;
    private List<MaterialCardView>contenedoresComida=new ArrayList<>();
    private DialogDataLink listener;
    private Context context;

    public RecyclerViewComidaFrecuenteAdapter(List<Comida> comidasFrecuentes, DialogDataLink listener, Context context) {
        this.listener = listener;
        this.comidasFrecuentes = comidasFrecuentes;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_elemento_comidafrecuente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comida comida = comidasFrecuentes.get(position);
        holder.contenedorComida.setStrokeColor(Color.TRANSPARENT);
        holder.nombreComida.setText(comida.getNombre());
        holder.calorias.setText(comida.getCalorias()+" cal");
        holder.fotoComida.setImageResource(comida.getImagenId());
        contenedoresComida.add(holder.contenedorComida);
        holder.contenedorComida.setOnClickListener(view -> {
            seleccionarComida(comida,holder.contenedorComida);
        });
    }

    @Override
    public int getItemCount() {
        return comidasFrecuentes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreComida;
        TextView calorias;
        ImageView fotoComida;
        MaterialCardView contenedorComida;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreComida = itemView.findViewById(R.id.nombreComida);
            calorias = itemView.findViewById(R.id.calorias);
            fotoComida=itemView.findViewById(R.id.fotoComida);
            contenedorComida=itemView.findViewById(R.id.contenedorComida);
        }
    }

    private void seleccionarComida(Comida comida,MaterialCardView contenedorComida){
        for(MaterialCardView card:contenedoresComida){
            card.setStrokeColor(Color.TRANSPARENT);
        }
        contenedorComida.setStrokeColor(Color.GREEN);
        HashMap<String,Object>data=new HashMap<>();
        data.put("nombre",comida.getNombre());
        data.put("calorias",comida.getCalorias());
        listener.inputLink(data);
    }
}
