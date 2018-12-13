package dev.mad.ussd4etecsa.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import dev.mad.ussd4etecsa.receibers.NetworkCheckReceiver;
import dev.mad.ussd4etecsa.receibers.SmsObserver;

public class GeneralService extends Service {

    private BroadcastReceiver conection;

    public GeneralService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("SERVICIOGENERAL", "Servicio creado...");
        SmsObserver myObserver = new SmsObserver(new Handler(), GeneralService.this);
        ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, myObserver);
        conection = new NetworkCheckReceiver();
        this.registerReceiver(conection, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("generalservice", "Servicio iniciado...");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(conection);
    }
}
