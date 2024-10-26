package com.example.lab05_20213852.Main;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lab05_20213852.InterfazInicial.DialogRegistrarComida;
import com.example.lab05_20213852.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        b=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.button.setOnClickListener(view -> lanzarDialogRegistrarDatos());
        crearCanalNotificacion("recurrentesHigh","NotificacionesRecurrentesImportantes",NotificationManager.IMPORTANCE_HIGH,"Notificaciones que aparecen de forma recurrente y que son de importancia.");
        crearCanalNotificacion("recurrentesNormal","NotificacionesRecurrentes",NotificationManager.IMPORTANCE_DEFAULT,"Notificaciones que aparece de forma recurrente.");
        crearCanalNotificacion("reactivasHigh","NotificacionesReactivasImportantes",NotificationManager.IMPORTANCE_HIGH,"Notificaciones que aparecen de forma reactiva y que son de importancia.");
        crearCanalNotificacion("reactivasNormal","NotificacionesReactivas",NotificationManager.IMPORTANCE_DEFAULT,"Notificaciones que aparecen de forma reactiva.");
    }

    private void lanzarDialogRegistrarDatos(){
        DialogRegistrarDatos dialogRegistrarAdministrador=new DialogRegistrarDatos();
        dialogRegistrarAdministrador.show(getSupportFragmentManager(),"dialogRegistrarDatos");
    }

    private void crearCanalNotificacion(String channelId,String channelName,Integer importancia,String channelDescription) {
        NotificationChannel channel=new NotificationChannel(channelId,channelName,importancia);
        channel.setDescription(channelDescription);
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        pedirPermiso();
    }

    private void pedirPermiso(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU&& ActivityCompat.checkSelfPermission(this,POST_NOTIFICATIONS)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{POST_NOTIFICATIONS},101);
        }
    }
}