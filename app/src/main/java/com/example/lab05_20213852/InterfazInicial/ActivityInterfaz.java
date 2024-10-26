package com.example.lab05_20213852.InterfazInicial;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab05_20213852.Notificacion;
import com.example.lab05_20213852.Objetos.ActividadFisica;
import com.example.lab05_20213852.Objetos.Comida;
import com.example.lab05_20213852.Linkers.DialogDataLink;
import com.example.lab05_20213852.R;
import com.example.lab05_20213852.Threads;
import com.example.lab05_20213852.databinding.ActivityInterfazBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ActivityInterfaz extends AppCompatActivity implements DialogDataLink {
    ActivityInterfazBinding b;
    private Integer caloriasRecomendadas;
    private Integer caloriasConsumidas=0;
    private Integer caloriasRestantes;
    private List<Comida>comidasDesayuno=new ArrayList<>();
    private List<Comida>comidasAlmuerzo=new ArrayList<>();
    private List<Comida>comidasCena=new ArrayList<>();
    private List<ActividadFisica>actividadesFisicas=new ArrayList<>();
    private RecyclerViewComidaAdapter recyclerViewDesayunoAdapter;
    private RecyclerViewComidaAdapter recyclerViewAlmuerzoAdapter;
    private RecyclerViewComidaAdapter recyclerViewCenaAdapter;
    private RecyclerViewActividadFisicaAdapter recyclerViewActividadesFisicasAdapter;
    private Integer minuto;
    private Integer hora;
    private Integer timestampTotal;
    private Boolean superoRecomendacion=false;
    private Integer objetivo;
    private Integer horaDelDia; //desayuno: 3AM 12PM, almuerzo: 12PM 6PM y cena 6PM 3AM
    private Integer periodoNotificaciones=55; //cada cuántos minutos se lanza la notificación motivacional
    private Integer detectorNuevaNotificacion=0;
    private final static Integer horaInicial=0; //la hora a la que empieza la hora en la aplicación
    private final static Integer minutoInicial=0; //el minuto al que empieza la hora en la aplicacion. Podrían ser a la hora actual real, pero que sean manuales permite hacer pruebas más variadas.
    private final static Double acelerador=1000.0; //el acelerador aumenta la velocidad a la que pasa el tiempo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        b=ActivityInterfazBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Intent intent=getIntent();
        caloriasRecomendadas=intent.getIntExtra("caloriasRecomendadas",0);
        b.caloriasRecomendadas.setText(caloriasRecomendadas+" kcal");
        b.caloriasRestantes.setText(caloriasRecomendadas+" kcal");
        b.periodoNotificaciones.setText(periodoNotificaciones+" min");
        objetivo=intent.getIntExtra("objetivo",-1);
        switch (objetivo){
            case 0:
                b.objetivo.setText("Subir de peso");
                break;
            case 1:
                b.objetivo.setText("Bajar de peso");
                break;
            case 2:
                b.objetivo.setText("Mantener peso");
                break;
        }
        recyclerViewDesayunoAdapter =new RecyclerViewComidaAdapter(comidasDesayuno);
        b.recyclerViewDesayuno.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        b.recyclerViewDesayuno.setAdapter(recyclerViewDesayunoAdapter);

        recyclerViewAlmuerzoAdapter=new RecyclerViewComidaAdapter(comidasAlmuerzo);
        b.recyclerViewAlmuerzo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        b.recyclerViewAlmuerzo.setAdapter(recyclerViewAlmuerzoAdapter);

        recyclerViewCenaAdapter =new RecyclerViewComidaAdapter(comidasCena);
        b.recyclerViewCena.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        b.recyclerViewCena.setAdapter(recyclerViewCenaAdapter);

        recyclerViewActividadesFisicasAdapter=new RecyclerViewActividadFisicaAdapter(actividadesFisicas);
        b.recyclerViewActividadFisica.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        b.recyclerViewActividadFisica.setAdapter(recyclerViewActividadesFisicasAdapter);

        b.botonRegistrarComida.setOnClickListener(view -> registrarComida());
        b.botonRegistrarActividadFisica.setOnClickListener(view -> registrarActividadFisica());
        b.botonConfigurarNotificaciones.setOnClickListener(view -> lanzarDialogCambiarPeriodoNotificaciones());

        timestampTotal=60*horaInicial+minutoInicial;

        horaDelDia=obtenerHoraDelDia(timestampTotal);

        switch (horaDelDia){
            case 0:
                b.horaDelDia.setText("Hora del desayuno");
                b.horaDelDia.setTextColor(Color.parseColor("#FFEB3B"));
                break;
            case 1:
                b.horaDelDia.setText("Hora del almuerzo");
                b.horaDelDia.setTextColor(Color.parseColor("#3BFF89"));
                break;
            case 2:
                b.horaDelDia.setText("Hora de la cena");
                b.horaDelDia.setTextColor(Color.parseColor("#3B45FF"));
                break;
        }

        Threads threads=(Threads) getApplication();
        ExecutorService executorService=threads.executorService;

        executorService.execute(() -> {
            while (true){
                runOnUiThread(()->{//este método se consiguió con ayuda de un LLM para poder manejar eventos en un hilo aparte, pero mediante el hilo principal para que los views se trabajen correctamente
                    hora=timestampTotal/60;
                    minuto=timestampTotal%60;
                    b.horaActual.setText(obtenerHoraActual());
                    if(timestampTotal>=24*60){
                        if(comidasDesayuno.size()+comidasAlmuerzo.size()+comidasCena.size()==0){
                            Notificacion.lanzarNotificacion("recurrentesHigh", NotificationCompat.PRIORITY_HIGH,"Reporte diario: No registró comidas","No registró alguna comida en alguna hora del día. Debido a ello, no es posible estimar si ha podido cumplir con el límite recomendado de calorías para "+(objetivo==0?"subir de peso":objetivo==1?"bajar de peso":objetivo==2?"mantener tu peso":"no lo bugees :'v"), R.drawable.icongoodkcal,this,ActivityInterfaz.class);
                        }else if(superoRecomendacion){
                            Notificacion.lanzarNotificacion("recurrentesHigh", NotificationCompat.PRIORITY_HIGH,"Reporte diario: Sobrepasó la recomendación brindada","En base a las comidas y actividades físicas registradas en todo el día, se consiguió superar el límite recomendado para "+(objetivo==0?"subir de peso":objetivo==1?"bajar de peso":objetivo==2?"mantener tu peso":"no lo bugees :'v")+". En caso no estar de acuerdo con el resultado, se le recomienda reducir la cantidad de calorías en comida consumida en el día, o aumentar la actividad física realizada.", R.drawable.icongoodkcal,this,ActivityInterfaz.class);
                        }else {
                            Notificacion.lanzarNotificacion("recurrentesHigh", NotificationCompat.PRIORITY_HIGH,"Reporte diario: Estuvo dentro bajo la recomendación brindada","En base a las comidas y actividades físicas registradas en todo el día, se consiguió estar bajo el límite recomendado para "+(objetivo==0?"subir de peso":objetivo==1?"bajar de peso":objetivo==2?"mantener tu peso":"no lo bugees :'v")+". En caso no estar de acuerdo con el resultado, se le recomienda aumentar la cantidad de calorías en comida consumida en el día.", R.drawable.icongoodkcal,this,ActivityInterfaz.class);
                        }
                        reiniciar();
                    }
                    Integer horaDelDiaAux=obtenerHoraDelDia(timestampTotal);
                    if(horaDelDiaAux!=horaDelDia){
                        switch (horaDelDiaAux){
                            case 0:
                                Notificacion.lanzarNotificacion("recurrentesHigh", NotificationCompat.PRIORITY_HIGH,"¡Hora del desayuno!","No se olvide de consumir un desayuno saludable e indicado para sus objetivos, así como de registrarlo en la aplicación.", R.drawable.icongoodkcal,this,ActivityInterfaz.class);
                                b.horaDelDia.setText("Hora del desayuno");
                                b.horaDelDia.setTextColor(Color.parseColor("#FFEB3B"));
                                break;
                            case 1:
                                Notificacion.lanzarNotificacion("recurrentesHigh", NotificationCompat.PRIORITY_HIGH,"¡Hora del almuerzo!","No se olvide de consumir un almuerzo saludable e indicado para sus objetivos, así como de registrarlo en la aplicación.", R.drawable.icongoodkcal,this,ActivityInterfaz.class);
                                b.horaDelDia.setText("Hora del almuerzo");
                                b.horaDelDia.setTextColor(Color.parseColor("#3BFF89"));
                                break;
                            case 2:
                                Notificacion.lanzarNotificacion("recurrentesHigh", NotificationCompat.PRIORITY_HIGH,"¡Hora de la cena!","No se olvide de consumir una cena saludable e indicada para sus objetivos, así como de registrarlo en la aplicación.", R.drawable.icongoodkcal,this,ActivityInterfaz.class);
                                b.horaDelDia.setText("Hora de la cena");
                                b.horaDelDia.setTextColor(Color.parseColor("#3B45FF"));
                                break;
                        }
                    }
                    Integer auxDetectorNuevaNotificacion= Math.toIntExact(Math.round(((timestampTotal*1.0/periodoNotificaciones) - 0.5)));
                    if(auxDetectorNuevaNotificacion!=detectorNuevaNotificacion){
                        Log.d("aaaa","olauwu");
                        detectorNuevaNotificacion=auxDetectorNuevaNotificacion;
                        lanzarNotificacionMotivacion();
                    }
                    horaDelDia=horaDelDiaAux;
                });
                timestampTotal++;
                try {
                    Thread.sleep(Math.round(60*1000/acelerador));
                }catch (InterruptedException ex){
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void reiniciar(){
        timestampTotal=0;
        comidasDesayuno.clear();
        comidasAlmuerzo.clear();
        comidasCena.clear();
        actividadesFisicas.clear();
        recyclerViewDesayunoAdapter.notifyDataSetChanged();
        recyclerViewAlmuerzoAdapter.notifyDataSetChanged();
        recyclerViewCenaAdapter.notifyDataSetChanged();
        recyclerViewActividadesFisicasAdapter.notifyDataSetChanged();
        caloriasConsumidas=0;
        b.caloriasConsumidas.setText("0 kcal");
        caloriasRestantes=caloriasRecomendadas;
        b.caloriasRestantes.setText(caloriasRestantes.toString()+" kcal");
    }

    private void registrarComida(){
        DialogRegistrarComida dialogRegistrarComida=new DialogRegistrarComida();
        dialogRegistrarComida.show(getSupportFragmentManager(),"dialogRegistrarComida");
    }

    private void registrarActividadFisica(){
        DialogRegistrarActividadFisica dialogRegistrarActividadFisica=new DialogRegistrarActividadFisica();
        dialogRegistrarActividadFisica.show(getSupportFragmentManager(),"dialogRegistrarActividadFisica");
    }

    @Override
    public void inputLink(HashMap<String, Object> data) {
        Integer dialog=(Integer) data.get("dialog");
        switch (dialog){
            case 0:
                Comida comida=(Comida)data.get("comida");
                if(comida.getTiempo()==null){
                    comida.setTiempo(obtenerHoraActual());
                }
                String tiempo=comida.getTiempo();
                String[]aux=tiempo.split(":");
                Integer timestamp=60*Integer.parseInt(aux[0])+Integer.parseInt(aux[1]);
                Integer horaDelDia=obtenerHoraDelDia(timestamp);
                switch (horaDelDia){
                    case 0:
                        comidasDesayuno.add(comida);
                        recyclerViewDesayunoAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        comidasAlmuerzo.add(comida);
                        recyclerViewAlmuerzoAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        comidasCena.add(comida);
                        recyclerViewCenaAdapter.notifyDataSetChanged();
                        break;
                }
                recalcularCaloriasTotales();
                break;
            case 1:
                ActividadFisica actividadFisica=(ActividadFisica) data.get("actividadFisica");
                if(actividadFisica.getTiempo()==null){
                    actividadFisica.setTiempo(obtenerHoraActual());
                }
                actividadesFisicas.add(actividadFisica);
                recyclerViewActividadesFisicasAdapter.notifyDataSetChanged();
                recalcularCaloriasTotales();
                break;
            default:
                break;
        }
    }

    private void recalcularCaloriasTotales(){
        Integer caloriasAux=0;
        for(Comida comida:comidasDesayuno){
            caloriasAux+=comida.getCalorias();
        }
        for(Comida comida:comidasAlmuerzo){
            caloriasAux+=comida.getCalorias();
        }
        for(Comida comida:comidasCena){
            caloriasAux+=comida.getCalorias();
        }
        for(ActividadFisica actividadFisica:actividadesFisicas){
            caloriasAux-=actividadFisica.getGastoCalorico();
        }
        caloriasConsumidas=caloriasAux;
        b.caloriasConsumidas.setText(caloriasConsumidas+" kcal");
        caloriasRestantes=caloriasRecomendadas-caloriasConsumidas;

        if(caloriasRestantes<0){
            b.caloriasRestantes.setText(""+-1*caloriasRestantes+" kcal");
            if(!superoRecomendacion){
                b.textoFaltaDe.setText("Sobredosis de: ");
                superoRecomendacion=true;
                Notificacion.lanzarNotificacion("reactivasHigh", NotificationCompat.PRIORITY_HIGH,"Sobre la cantidad de calorías recomendadas","Debido a los alimentos consumidos, se encuentra sobre el nivel recomendado de calorías para "+(objetivo==0?"subir de peso":objetivo==1?"bajar de peso":objetivo==2?"mantener tu peso":"no lo bugees :'v")+".", R.drawable.iconhighkcal,this,ActivityInterfaz.class);
            }
        }else {
            b.caloriasRestantes.setText(""+caloriasRestantes+" kcal");
            if(superoRecomendacion){
                b.textoFaltaDe.setText("Falta de: ");
                superoRecomendacion=false;
                Notificacion.lanzarNotificacion("reactivasHigh", NotificationCompat.PRIORITY_HIGH,"Bajo la cantidad de calorías recomendadas","Debido a las actividades físicas realizadas, se encuentra bajo el nivel recomendado de calorías para "+(objetivo==0?"subir de peso":objetivo==1?"bajar de peso":objetivo==2?"mantener tu peso":"no lo bugees :'v")+".", R.drawable.iconlowkcal,this,ActivityInterfaz.class);
            }
        }
    }

    private void lanzarNotificacionMotivacion(){
        Integer numeroRandom= (int) Math.floor(Math.random()*3);
        String titulo;
        String texto;
        if(!superoRecomendacion){
            switch (numeroRandom){
                case 0:
                    titulo="¡Estás en el camino correcto!";
                    texto="Tus niveles de calorías están un poco bajos hoy. Considera añadir una merienda nutritiva para mantener tu energía óptima. ¡Tú puedes lograrlo!";
                    break;
                case 1:
                    titulo="Gran trabajo, ¡pero puedes dar un poco más!";
                    texto="¡Vas muy bien! Solo necesitas un poco más de energía para cubrir tus necesidades diarias. Añade un snack saludable y sigue adelante.";
                    break;
                case 2:
                    titulo="¡Casi alcanzas tu meta de energía diaria!";
                    texto="Tus esfuerzos están dando frutos. Recuerda cuidar de ti y alcanzar un equilibrio con pequeñas comidas saludables.";
                    break;
                default:
                    titulo="Por qué lo bugeas:(";
                    texto="Eres niño malo.";
                    break;
            }
        }else {
            switch (numeroRandom){
                case 0:
                    titulo="¡Gran esfuerzo hoy!";
                    texto="Hoy has superado ligeramente tus calorías recomendadas. ¡Es un buen momento para mantenerte activo! Con una caminata ligera, te acercarás aún más a tus metas.";
                    break;
                case 1:
                    titulo="¡Estás avanzando con fuerza!";
                    texto="Has alcanzado tu objetivo de energía del día. Mantente en movimiento y busca opciones saludables para equilibrar el consumo de calorías. ¡Vas por buen camino!";
                    break;
                case 2:
                    titulo="¡Buen trabajo! Vamos a cuidar los pequeños excesos.";
                    texto="Tus niveles de energía están un poco altos hoy. ¡Nada que una actividad ligera no pueda balancear! Mantén el buen ritmo, estás logrando grandes cosas.";
                    break;
                default:
                    titulo="Por qué lo bugeas:(";
                    texto="Eres niño malo.";
                    break;
            }
        }
        Notificacion.lanzarNotificacion("recurrentesNormal", NotificationCompat.PRIORITY_DEFAULT,titulo,texto, R.drawable.icongoodkcal,this,ActivityInterfaz.class);
    }
    public String obtenerHoraActual() {
        return String.format("%02d:%02d", hora, minuto);
    }

    public Integer obtenerHoraDelDia(Integer timestamp){
        if((timestamp<=24*60&&timestamp>=18*60)||(timestamp>=0&&timestamp<3*60)){
            return 2;
        }else if(timestamp<18*60&&timestamp>=12*60){
            return 1;
        }else if(timestamp<12*60&&timestamp>=3*60){
            return 0;
        }else {
            return null;
        }
    }

    private void lanzarDialogCambiarPeriodoNotificaciones() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textInputLayout.setHint("Ingrese el nuevo periodo de notificaciones");

        TextInputEditText editText = new TextInputEditText(this);
        editText.setId(View.generateViewId());
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        textInputLayout.addView(editText);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 16);
        layout.addView(textInputLayout);

        dialogBuilder.setView(layout)
                .setTitle("Cambiar periodo entre notificaciones de motivación")
                .setMessage("Ingrese el nuevo periodo que desea entre notificaciones de motivación que reciba, acerca del resultado actual de calorías consumidas.")
                .setPositiveButton("Cambiar", (dialog, id) -> {
                    periodoNotificaciones = Integer.parseInt(editText.getText().toString());
                    b.periodoNotificaciones.setText(periodoNotificaciones+" min");
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss())
                .show();
    }
}