package dev.mad.ussd4etecsa.settings;


import android.app.NotificationManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;


import androidx.annotation.RequiresApi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import dev.mad.ussd4etecsa.models.AuxConfigModel;
import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.R;
import dev.mad.ussd4etecsa.notification.NotificationHelper;

public class ConfigActivity extends AppCompatActivity {


    //Defino los iconos de la sendNotifacation en la barra de sendNotifacation  
    SharedPreferences sp;
    DatabaseHelper dbHelper;
    AuxConfigModel modelConfig = new AuxConfigModel();
    NotificationHelper notificationHelper;
    RelativeLayout cv_acceso;
    Switch sNotify;

    Switch cCall;
    Switch cSMS;
    Switch cDat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = findViewById(R.id.toolbarConfig);
        toolbar.setTitle("Configuración");
        setSupportActionBar(toolbar);

        //Se inicializa la BD
        dbHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        notificationHelper = new NotificationHelper(getApplicationContext());

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        cv_acceso = findViewById(R.id.cv_accesibilidad);


        sp = this.getSharedPreferences("ussdPreferences", MODE_PRIVATE);

        sNotify = findViewById(R.id.sw_config_notification);
        cCall = findViewById(R.id.cbx_config_call);
        cSMS = findViewById(R.id.cbx_config_sms);
        cDat = findViewById(R.id.cbx_config_datos);

        cCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.edit().putBoolean("cCall", true).apply();
                } else {
                    sp.edit().putBoolean("cCall", false).apply();
                }
            }
        });

        cSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    sp.edit().putBoolean("cSMS", true).apply();

                } else {
                    sp.edit().putBoolean("cSMS", false).apply();
                }
            }
        });

        cDat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.edit().putBoolean("cDatos", true).apply();
                } else {
                    sp.edit().putBoolean("cDatos", false).apply();
                }
            }
        });


        sNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sendNotifacation();
                    try {
                        modelConfig.updateConfig("NOTIFICATION", "1", getApplicationContext());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                } else {
                    notificationHelper.closeNotification();
                    try {
                        modelConfig.updateConfig("NOTIFICATION", "0", getApplicationContext());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }


            }
        });


        cv_acceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
    }

    /**
     * Control de aplicacion
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        boolean chxStateCall = sp.getBoolean("cCall", true);
        boolean chxStateSms = sp.getBoolean("cSMS", true);
        boolean chxStateDatos = sp.getBoolean("cDatos", true);
        cCall.setChecked(chxStateCall);
        cSMS.setChecked(chxStateSms);
        cDat.setChecked(chxStateDatos);

        try {
            if (modelConfig.existeConfig("NOTIFICATION", getApplicationContext())) {

                if (modelConfig.getValorConfig("NOTIFICATION").equals("1")) {
                    sNotify.setChecked(true);
                } else {
                    sNotify.setChecked(false);
                }
            } else {
                sNotify.setChecked(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    /**
     * Crear notificaciones en la barra de estado con el saldo
     */
    public void sendNotifacation() {
        notificationHelper.sendUpdateNotificacion();

    }


}
