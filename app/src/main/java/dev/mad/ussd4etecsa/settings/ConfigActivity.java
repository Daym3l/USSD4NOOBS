package dev.mad.ussd4etecsa.settings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import dev.mad.ussd4etecsa.models.AuxConfigModel;
import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.models.UssdModel;
import dev.mad.ussd4etecsa.Nav_Principal;
import dev.mad.ussd4etecsa.R;

public class ConfigActivity extends AppCompatActivity {

    // Constante con ID de la notificacion
    private static final int NOTIFICATION_ID = 1;

    //    Variables de la sendNotifacation
    NotificationManager adminNotifications;
    static String servNotifications = Context.NOTIFICATION_SERVICE;

    //Defino los iconos de la sendNotifacation en la barra de sendNotifacation
    int iconNotificactionApp = R.drawable.ic_notification;
    SharedPreferences sp;
    DatabaseHelper dbHelper;
    AuxConfigModel modelConfig = new AuxConfigModel();
    UssdModel modelUssd = new UssdModel();
    RelativeLayout cv_acceso;
    Switch sNotify;

    Switch cCall;
    Switch cSMS;
    Switch cDat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfig);
        toolbar.setTitle("Configuración");
        setSupportActionBar(toolbar);

        //Se inicializa la BD
        dbHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        cv_acceso = (RelativeLayout) findViewById(R.id.cv_accesibilidad);

        // Inicio el servicio de notificaciones accediendo al servicio
        adminNotifications = (NotificationManager) getSystemService(servNotifications);

        sp = this.getSharedPreferences("ussdPreferences", this.MODE_PRIVATE);

        sNotify = (Switch) findViewById(R.id.sw_config_notification);
        cCall = (Switch) findViewById(R.id.cbx_config_call);
        cSMS = (Switch) findViewById(R.id.cbx_config_sms);
        cDat = (Switch) findViewById(R.id.cbx_config_datos);


        cCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.edit().putBoolean("cCall", true).commit();
                } else {
                    sp.edit().putBoolean("cCall", false).commit();
                }
            }
        });

        cSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                        sp.edit().putBoolean("cSMS", true).commit();

                } else {
                    sp.edit().putBoolean("cSMS", false).commit();
                }
            }
        });

        cDat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.edit().putBoolean("cDatos", true).commit();
                } else {
                    sp.edit().putBoolean("cDatos", false).commit();
                }
            }
        });


        sNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    try {
                        sendNotifacation();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        try {
                            modelConfig.updateConfig("NOTIFICATION", "1", getApplicationContext());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }


                } else {

                    adminNotifications.cancel(NOTIFICATION_ID);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        try {
                            modelConfig.updateConfig("NOTIFICATION", "0", getApplicationContext());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (verificarNotifications()) {
                sNotify.setChecked(true);
                try {
                    resetNotication();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        } else {
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

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean verificarNotifications() {
        final StatusBarNotification[] activeNotifications = adminNotifications.getActiveNotifications();
        boolean flag = false;
        for (StatusBarNotification notification : activeNotifications) {
            if (notification.getId() == NOTIFICATION_ID) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * Resetear Notificaciones
     */
    public void resetNotication() throws SQLException {
        adminNotifications.cancel(NOTIFICATION_ID);
        sendNotifacation();
    }


    /**
     * Crear notificaciones en la barra de estado con el saldo
     */
    public void sendNotifacation() throws SQLException {
        // Capturo la hora del evento
        long hora = System.currentTimeMillis();
        // Conexion a la BD
        String valorSaldo = modelUssd.getValor("SALDO", getApplicationContext());
        String saldos = "Saldo principal: " + valorSaldo;
        // Definimos la accion de la pulsacion sobre la sendNotifacation (esto es opcional)
        Context context = getApplicationContext();
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        Intent notificationIntent = new Intent(this, Nav_Principal.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        nBuilder.setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setSmallIcon(iconNotificactionApp).setWhen(hora)
                .setContentTitle("Saldo Disponible")
                .setContentText(saldos);


        Notification notification = nBuilder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        adminNotifications.notify(NOTIFICATION_ID, notification);

    }


}
