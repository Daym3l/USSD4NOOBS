package dev.mad.ussd4etecsa.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import dev.mad.ussd4etecsa.utiles.Constantes;

/**
 * Created by Daymel on 3/6/2017.
 */

public class UssdService extends Service {

    private static final String TAG = UssdService.class.getSimpleName();
    TimerTask timerTask;

    public UssdService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Servicio creado...");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Servicio iniciado...");



        Timer timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent localIntent = new Intent(Constantes.ACTION_RUN_SERVICE);

                // Emitir el intent a la actividad
                LocalBroadcastManager.getInstance(UssdService.this).sendBroadcast(localIntent);
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timerTask.cancel();
        Intent localIntent = new Intent(Constantes.ACTION_USSD_EXIT);

        // Emitir el intent a la actividad
        LocalBroadcastManager.getInstance(UssdService.this).sendBroadcast(localIntent);
        Log.d(TAG, "Servicio destruido...");
    }

}
