package com.example.lab05_20213852.InterfazInicial;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab05_20213852.Objetos.Comida;
import com.example.lab05_20213852.Linkers.DialogDataLink;
import com.example.lab05_20213852.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.HashMap;

public class DialogRegistrarComida extends DialogFragment implements DialogDataLink {


    private TextView cerrarDialog;
    private TextInputEditText inputNombre;
    private TextInputEditText inputCalorias;
    private TextInputEditText inputHora;
    private RecyclerView recyclerViewComidaFrecuente;
    private RecyclerViewComidaFrecuenteAdapter adapter;
    private Button botonRegistrar;
    private DialogDataLink listener;
    private String nombre;
    private String calorias;
    private String hora;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogDataLink) context;
        } catch (ClassCastException e) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_registrar_comida, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cerrarDialog = dialog.findViewById(R.id.botonCerrarDialog);
            inputNombre=dialog.findViewById(R.id.inputNombre);
            inputCalorias=dialog.findViewById(R.id.inputCalorias);
            inputHora = dialog.findViewById(R.id.inputHora);
            recyclerViewComidaFrecuente=dialog.findViewById(R.id.recyclerViewComidaFrecuente);
            recyclerViewComidaFrecuente.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            Comida comidaFrecuente1=new Comida();
            comidaFrecuente1.setNombre("Salchipapas");
            comidaFrecuente1.setCalorias(1000);
            comidaFrecuente1.setImagenId(R.drawable.salchipapa);

            Comida comidaFrecuente2=new Comida();
            comidaFrecuente2.setNombre("Yogur con cereales");
            comidaFrecuente2.setCalorias(200);
            comidaFrecuente2.setImagenId(R.drawable.yogurconcereales);

            Comida comidaFrecuente3=new Comida();
            comidaFrecuente3.setNombre("Arroz con pollo");
            comidaFrecuente3.setCalorias(750);
            comidaFrecuente3.setImagenId(R.drawable.arrozconpollo);

            Comida comidaFrecuente4=new Comida();
            comidaFrecuente4.setNombre("Pescado frito");
            comidaFrecuente4.setCalorias(250);
            comidaFrecuente4.setImagenId(R.drawable.pescadofrito);

            Comida comidaFrecuente5=new Comida();
            comidaFrecuente5.setNombre("Ceviche");
            comidaFrecuente5.setCalorias(420);
            comidaFrecuente5.setImagenId(R.drawable.ceviche);

            adapter=new RecyclerViewComidaFrecuenteAdapter(Arrays.asList(comidaFrecuente1,comidaFrecuente2,comidaFrecuente3,comidaFrecuente4,comidaFrecuente5),this,getContext());
            recyclerViewComidaFrecuente.setAdapter(adapter);

            botonRegistrar = dialog.findViewById(R.id.botonRegistrar);
            cerrarDialog=dialog.findViewById(R.id.botonCerrarDialog);
            cerrarDialog.setOnClickListener(view -> dismiss());
            inputNombre.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    nombre=inputNombre.getText().toString().trim();
                    activarBotonRegistrar();
                }
            });
            inputCalorias.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    calorias=inputCalorias.getText().toString().trim();
                    activarBotonRegistrar();
                }
            });
            inputHora.setFocusable(false);
            inputHora.setClickable(true);
            inputHora.setInputType(InputType.TYPE_NULL);
            inputHora.setOnClickListener(view -> seleccionarHora());

            botonRegistrar.setElevation(8);

            botonRegistrar.setOnClickListener(view -> {
                if(botonRegistrar.isEnabled()){
                    registrarComida();
                }
            });
        }


    }
    private void activarBotonRegistrar(){
        if(nombre!=null&&!nombre.isBlank()&&calorias!=null&&!calorias.isBlank()){
            botonRegistrar.setEnabled(true);
            botonRegistrar.setAlpha(1);
        }else {
            botonRegistrar.setEnabled(false);
            botonRegistrar.setAlpha(0.5f);
        }
    }
    private void registrarComida(){
        HashMap<String,Object>data=new HashMap<>();
        data.put("dialog",0);
        Comida comida=new Comida();
        comida.setCalorias(Integer.parseInt(calorias));
        comida.setNombre(nombre);
        if(hora==null||hora.isBlank()){
            comida.setTiempo(null);
        }else {
            comida.setTiempo(hora);
        }
        data.put("comida",comida);
        listener.inputLink(data);
        dismiss();
    }


    @Override
    public void inputLink(HashMap<String, Object> data) {
        nombre=(String) data.get("nombre");
        calorias= data.get("calorias").toString();
        inputNombre.setText(nombre);
        inputCalorias.setText(calorias);
        activarBotonRegistrar();
    }

    private void seleccionarHora() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, selectedHour, selectedMinute) -> {
                    hora = String.format("%02d:%02d", selectedHour, selectedMinute);
                    inputHora.setText(hora);
                }, hour, minute, true);
        timePickerDialog.show();
        activarBotonRegistrar();
    }
}
