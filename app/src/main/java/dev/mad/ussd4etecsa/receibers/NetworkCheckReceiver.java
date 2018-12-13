package dev.mad.ussd4etecsa.receibers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import java.sql.SQLException;

import dev.mad.ussd4etecsa.models.UssdModel;
import dev.mad.ussd4etecsa.notification.NotificationHelper;

/**
 * Created by Daymel on 25/4/2018.
 */

public class NetworkCheckReceiver extends BroadcastReceiver {

    private UssdModel ussdModel = new UssdModel();


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("NetworkCheckReceiver", "NetworkCheckReceiver invoked...");


            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            final SharedPreferences sp = context.getSharedPreferences("ussdPreferences", context.MODE_PRIVATE);
            boolean chxStateDatos = sp.getBoolean("cDatos", true);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            Bundle extras = intent.getExtras();
            String reason = (String) extras.get("reason");
            String type = (extras.get("extraInfo") != null) ? (String) extras.get("extraInfo") : "nauta";
            Boolean state = (Boolean) extras.get("noConnectivity");
            String action = intent.getStringExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
            if (reason != null) {
                if (reason.equals("specificDisabled")) {
                    if (noConnectivity && type.equals("nauta") && sp.getBoolean("conectado", true)) {
                        Log.d("NetworkCheckReceiver", "disconnected");
                        sp.edit().putBoolean("conectado", false).commit();
                        if (chxStateDatos) {
                            new CountDownTimer(5000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    //here you can have your logic to set text to edittext
                                }

                                public void onFinish() {
                                    try {
                                        accionDemorada(context, "net");

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }.start();
                        }
                    }
                } else {
                    sp.edit().putBoolean("conectado", false).commit();
                }
            } else {
                if (!noConnectivity && type.equals("nauta")) {
                    sp.edit().putBoolean("conectado", true).commit();
                }
            }


        }
    }

    private void accionDemorada(final Context context, String accion) throws SQLException {
        String ussdCod = "222";

        if (!getValorSaldos("BOLSA", context).equals("0.00")) {
            ussdCod = "222*328";
        }


        marcarNumero(ussdCod, context);
        final NotificationHelper notificationHelper = new NotificationHelper(context);
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                notificationHelper.sendUpdateNotificacion();
            }

        }.start();

    }

    /**
     * @param accion
     * @param context
     * @return
     * @throws SQLException
     */
    private String getValorSaldos(String accion, Context context) throws SQLException {
        String saldoCall = "";
        saldoCall = ussdModel.getValor(accion, context);
        return saldoCall;
    }

    /**
     * @param codigo
     * @param context
     */
    private void marcarNumero(String codigo, Context context) {


        String ussdCodigo = "*" + codigo + Uri.encode("#");
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}