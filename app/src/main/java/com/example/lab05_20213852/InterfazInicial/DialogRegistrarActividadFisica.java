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
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab05_20213852.Linkers.DialogDataLink;
import com.example.lab05_20213852.Objetos.ActividadFisica;
import com.example.lab05_20213852.Objetos.Comida;
import com.example.lab05_20213852.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

public class DialogRegistrarActividadFisica extends DialogFragment{


    private TextView cerrarDialog;
    private TextInputEditText inputNombre;
    private TextInputEditText inputGastoCalorico;
    private TextInputEditText inputHora;
    private Button botonRegistrar;
    private DialogDataLink listener;
    private String nombre;
    private String gastoCalorico;
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
        return inflater.inflate(R.layout.dialog_registrar_actividad_fisica, container, false);
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
            inputGastoCalorico=dialog.findViewById(R.id.inputGastoCalorico);
            inputHora = dialog.findViewById(R.id.inputHora);
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
            inputGastoCalorico.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    gastoCalorico=inputGastoCalorico.getText().toString().trim();
                    activarBotonRegistrar();
                }
            });
            inputHora.setInputType(InputType.TYPE_NULL);
            inputHora.setOnClickListener(view -> seleccionarHora());

            botonRegistrar.setElevation(8);

            botonRegistrar.setOnClickListener(view -> {
                if(botonRegistrar.isEnabled()){
                    registrarActividadFisica();
                }
            });
        }


    }
    private void activarBotonRegistrar(){
        if(nombre!=null&&!nombre.isBlank()&&gastoCalorico!=null&&!gastoCalorico.isBlank()){
            botonRegistrar.setEnabled(true);
            botonRegistrar.setAlpha(1);
        }else {
            botonRegistrar.setEnabled(false);
            botonRegistrar.setAlpha(0.5f);
        }
    }
    private void registrarActividadFisica(){
        HashMap<String,Object>data=new HashMap<>();
        data.put("dialog",1);
        ActividadFisica actividadFisica=new ActividadFisica();
        actividadFisica.setGastoCalorico(Integer.parseInt(gastoCalorico));
        actividadFisica.setNombre(nombre);
        if(hora==null||hora.isBlank()){
            actividadFisica.setTiempo(null);
        }else {
            actividadFisica.setTiempo(hora);
        }
        data.put("actividadFisica",actividadFisica);
        listener.inputLink(data);
        dismiss();
    }


    private void seleccionarHora() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, selectedHour, selectedMinute) -> {
                    String hora = String.format("%02d:%02d", selectedHour, selectedMinute);
                    inputHora.setText(hora);
                }, hour, minute, true);
        timePickerDialog.show();
        activarBotonRegistrar();
    }
}
