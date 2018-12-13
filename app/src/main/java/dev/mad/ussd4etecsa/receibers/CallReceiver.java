package dev.mad.ussd4etecsa.receibers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.sql.SQLException;

import dev.mad.ussd4etecsa.models.UssdModel;
import dev.mad.ussd4etecsa.notification.NotificationHelper;

/**
 * Created by Daymel on 11/7/2017.
 */

public class CallReceiver extends BroadcastReceiver {

    private String callState = "";
    boolean flag = false;
    private UssdModel ussdModel = new UssdModel();

    @Override
    public void onReceive(final Context context, Intent intent) {

        // verificacion para cuando se cuelga la llamada;
        callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        final SharedPreferences sp = context.getSharedPreferences("ussdPreferences", context.MODE_PRIVATE);
        boolean chxStateCall = sp.getBoolean("cCall", true);
//        boolean chxStateDatos = sp.getBoolean("cDatos", true);
      /*  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info.getTypeName().equals("MOBILE") && info.getState().equals(NetworkInfo.State.DISCONNECTING)) {

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

        }*/

        if (callState.equals(TelephonyManager.EXTRA_STATE_IDLE) && !LastCall(context).equals("0") && chxStateCall) {

            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    try {
                        accionDemorada(context, "call");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }.start();

        }

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


    /**
     * @param context
     * @param accion
     */
    private void accionDemorada(final Context context, String accion) throws SQLException {
        String ussdCod = "222";
        if (accion.equals("call")) {
            if (!getValorSaldos("VOZ", context).equals("0:00:00") && !getValorSaldos("VOZ", context).equals("0")) {
                ussdCod = "222*869";
            }
            if (!getValorSaldos("BONO", context).equals("00:00:00") && !getValorSaldos("BONO", context).equals("0.00")) {
                ussdCod = "222*266";
            }
        } else {
            if (!getValorSaldos("BOLSA", context).equals("0.00")) {
                ussdCod = "222*328";
            }
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

    public String LastCall(Context context) {
        StringBuffer sb = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Cursor cur = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");

        int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
        int duration = cur.getColumnIndex(CallLog.Calls.DURATION);

        while (cur.moveToNext()) {
            String phNumber = cur.getString(number);
            String callDuration = cur.getString(duration);
            sb.append(callDuration);
            break;
        }
        cur.close();
        String str = sb.toString();
        return str;
    }
}