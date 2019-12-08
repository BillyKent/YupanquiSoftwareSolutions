package com.safehome.disenosoft.safehome;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.safehome.disenosoft.safehome.Servicios.ConexionBD;
import com.safehome.disenosoft.safehome.Servicios.Habitante;
import com.safehome.disenosoft.safehome.Servicios.Rostro;

import java.util.ArrayList;

import static com.safehome.disenosoft.safehome.App.CHANNEL_ID;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    Notification notification = null;

    Habitante miHabitante = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        miHabitante = (Habitante) intent.getSerializableExtra("habitante");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("habitante",miHabitante);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Safe Home")
                .setContentText("Presione aquí para abrir la aplicación")
                .setSmallIcon(R.drawable.logo_safehome)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    ArrayList<Rostro> rostros = ConexionBD.getInstancia().ObtenerRostrosDetectados();

                    if(rostros != null){
                        crearNotificacion(rostros);
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    private void crearNotificacion(ArrayList<Rostro> rostros){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("habitante",miHabitante);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        boolean hayDesconocidos = false;
        String mensaje = "";

        if(rostros.size() < 2){
            Rostro r = rostros.get(0);
            if(r.isEsConocido()){
                mensaje = "¡" + r.getNombre() + " está en tu puerta!";
            }else{
                mensaje = "¡Hay un desconocido en tu puerta!";
            }
        }else{
            String nombres = "¡";
            for(int i = 0; i < rostros.size() ; i++){
                Rostro r = rostros.get(i);
                if(!r.isEsConocido()) {
                    hayDesconocidos = true;
                    break;
                }

                if(i == rostros.size() - 1){
                    nombres += r.getNombre();
                }else if (i == rostros.size() - 2){
                    nombres += r.getNombre() + " y ";
                }else{
                    nombres += r.getNombre() + ", ";
                }
            }
            mensaje = nombres + " están en tu puerta!";
        }

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Se han detectado rostros")
                .setContentText(mensaje)
                .setSmallIcon(R.drawable.logo_safehome)
                .setContentIntent(pendingIntent)
                .build();

        //stopForeground();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
