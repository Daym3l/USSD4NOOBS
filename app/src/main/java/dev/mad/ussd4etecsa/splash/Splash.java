package dev.mad.ussd4etecsa.splash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import dev.mad.ussd4etecsa.Nav_Principal;
import dev.mad.ussd4etecsa.R;

public class Splash extends AppCompatActivity {
    private int tiempo = 5000;
    SharedPreferences sharedPreferences;
    public static final int REQUEST_MULTIPLE_PERMISSIONS_ID = 456;
    Intent intentLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("ussdPreferences", Context.MODE_PRIVATE);
        boolean init = sharedPreferences.getBoolean("intro", false);
        if (checkPermiso()) {
            tiempo = 3000;
        }

        if (init) {
            intentLoad = new Intent(Splash.this, Nav_Principal.class);
        } else {
            intentLoad = new Intent(Splash.this, SwipeSplash.class);
        }
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(tiempo);

                    startActivity(intentLoad);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();


    }

    /**
     * Chequea los premisos necesarios para aplicacion
     */
    public boolean checkPermiso() {


        int permisoCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int permisoCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int permisoCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permisoCheck4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int permisoCheck5 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permisoCheck6 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        List<String> lista_permisos = new ArrayList<>();


        if (permisoCheck != PackageManager.PERMISSION_GRANTED) {
            lista_permisos.add(Manifest.permission.CALL_PHONE);
        }
        if (permisoCheck5 != PackageManager.PERMISSION_GRANTED) {
            lista_permisos.add(Manifest.permission.READ_CONTACTS);
        }

        if (permisoCheck3 != PackageManager.PERMISSION_GRANTED) {
            lista_permisos.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permisoCheck2 != PackageManager.PERMISSION_GRANTED) {
            lista_permisos.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (permisoCheck4 != PackageManager.PERMISSION_GRANTED) {
            lista_permisos.add(Manifest.permission.READ_SMS);
        }
        if (permisoCheck6 != PackageManager.PERMISSION_GRANTED) {
            lista_permisos.add(Manifest.permission.READ_CALL_LOG);
        }
        if (!lista_permisos.isEmpty()) {
            ActivityCompat.requestPermissions(this, lista_permisos.toArray(new String[lista_permisos.size()]), REQUEST_MULTIPLE_PERMISSIONS_ID);
            return false;
        }
        return true;

    }


}
