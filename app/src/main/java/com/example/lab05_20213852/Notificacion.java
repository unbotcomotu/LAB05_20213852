package com.example.lab05_20213852;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notificacion {
    public static void lanzarNotificacion(String channelId, Integer prioridad, String titulo, String texto, Integer iconoPequeno, Context contextoInicial, Class<?>activityDestino){
        Intent intent=new Intent(contextoInicial, activityDestino);
        PendingIntent pendingIntent=PendingIntent.getActivity(contextoInicial,0,intent,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(contextoInicial,channelId)
                .setSmallIcon(iconoPequeno)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(prioridad)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(contextoInicial);
        if(ActivityCompat.checkSelfPermission(contextoInicial,POST_NOTIFICATIONS)== PackageManager.PERMISSION_GRANTED){
            notificationManagerCompat.notify(1,builder.build());
        }
    }
}
