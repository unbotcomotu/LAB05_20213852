package com.example.lab05_20213852.Main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lab05_20213852.InterfazInicial.ActivityInterfaz;
import com.example.lab05_20213852.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class DialogRegistrarDatos extends DialogFragment {


    private TextView cerrarDialog;
    private TextInputEditText inputPeso;
    private TextInputEditText inputAltura;
    private TextInputEditText inputEdad;
    private MaterialAutoCompleteTextView selectorGenero;
    private MaterialAutoCompleteTextView selectorActividadFisica;
    private MaterialAutoCompleteTextView selectorObjetivo;
    private Button botonRegistrar;

    private String peso;
    private String altura;
    private String edad;
    private Integer genero;
    private Integer nivelActividad;
    private Integer objetivo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_registrar_datos, container, false);
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
            inputPeso=dialog.findViewById(R.id.inputPeso);
            inputAltura=dialog.findViewById(R.id.inputAltura);
            inputEdad = dialog.findViewById(R.id.inputEdad);
            selectorGenero = dialog.findViewById(R.id.selectorGenero);
            selectorActividadFisica = dialog.findViewById(R.id.selectorActividadFisica);
            selectorObjetivo = dialog.findViewById(R.id.selectorObjetivo);
            botonRegistrar = dialog.findViewById(R.id.botonRegistrar);
            cerrarDialog=dialog.findViewById(R.id.botonCerrarDialog);
            cerrarDialog.setOnClickListener(view -> dismiss());
            String[] generos = {"Masculino", "Femenino", "Prefiero no decirlo"};
            String[] nivelesActividadFisica = {"Poco o ningún ejercicio", "Ejercicio ligero (1 - 3 días por semana)", "Ejercicio moderado (3 - 5 días por semana)","Ejercicio fuerte (6 - 7 días por semana)","Ejercicio muy fuerte (dos veces al día)"};
            String[] objetivos = {"Subir peso", "Bajar peso", "Mantener peso"};
            ArrayAdapter<String> adapterGeneros = new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line, generos);
            ArrayAdapter<String> adapterNivelesActividadFisica = new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line, nivelesActividadFisica);
            ArrayAdapter<String> adapterObjetivos = new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line, objetivos);
            selectorGenero.setAdapter(adapterGeneros);
            selectorActividadFisica.setAdapter(adapterNivelesActividadFisica);
            selectorObjetivo.setAdapter(adapterObjetivos);


            inputPeso.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    peso=inputPeso.getText().toString().trim();
                    activarBotonRegistrar();
                }
            });
            inputAltura.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    altura=inputAltura.getText().toString().trim();
                    activarBotonRegistrar();
                }
            });
            inputEdad.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    edad=inputEdad.getText().toString().trim();
                    activarBotonRegistrar();
                }
            });

            selectorGenero.setOnItemClickListener((parent, v, position, id) -> {
                genero = position;
                activarBotonRegistrar();
            });
            selectorObjetivo.setOnItemClickListener((parent, v, position, id) -> {
                objetivo = position;
                activarBotonRegistrar();
            });
            selectorActividadFisica.setOnItemClickListener((parent, v, position, id) -> {
                nivelActividad = position;
                activarBotonRegistrar();
            });

            botonRegistrar.setElevation(8);

            botonRegistrar.setOnClickListener(view -> {
                if(botonRegistrar.isEnabled()){
                    ingresarInterfaz();
                }
            });
        }


    }
    private void activarBotonRegistrar(){
        if(peso!=null&&!peso.isBlank()&&altura!=null&&!altura.isBlank()&&edad!=null&&!edad.isBlank()&&genero!=null&&nivelActividad!=null&&objetivo!=null){
            botonRegistrar.setEnabled(true);
            botonRegistrar.setAlpha(1);
        }else {
            botonRegistrar.setEnabled(false);
            botonRegistrar.setAlpha(0.5f);
        }
    }
    private void ingresarInterfaz(){
        Intent intent=new Intent(getContext(), ActivityInterfaz.class);
        intent.putExtra("caloriasRecomendadas",obtenerCantidadCalorias());
        intent.putExtra("objetivo",objetivo);
        startActivity(intent);
    }

    private Integer obtenerCantidadCalorias(){
        Double calorias=10*Integer.parseInt(peso)+6.25*Integer.parseInt(altura)-5*Integer.parseInt(edad);
        calorias=genero==0?calorias+5:genero==1?calorias-161:0;
        switch (nivelActividad){
            case 0:
                calorias*=1.2;
                break;
            case 1:
                calorias*=1.375;
                break;
            case 2:
                calorias*=1.55;
                break;
            case 3:
                calorias*=1.725;
                break;
            case 4:
                calorias*=1.9;
                break;
            default:
                calorias=0.0;
                break;
        }
        switch (objetivo){
            case 0:
                calorias+=500;
                break;
            case 1:
                calorias-=300;
                break;
            case 2:
                break;
            default:
                calorias=0.0;
                break;
        }
        return Math.toIntExact(Math.round(calorias));
    }
}
