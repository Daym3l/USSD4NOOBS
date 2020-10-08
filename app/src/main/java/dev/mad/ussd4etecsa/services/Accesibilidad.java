package dev.mad.ussd4etecsa.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import dev.mad.ussd4etecsa.models.Tables.DatAmigo;
import dev.mad.ussd4etecsa.models.Tables.DatUssd;
import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.R;
import dev.mad.ussd4etecsa.updateSaldo.Bono;
import dev.mad.ussd4etecsa.updateSaldo.Datos;
import dev.mad.ussd4etecsa.updateSaldo.Saldo;
import dev.mad.ussd4etecsa.updateSaldo.Sms;
import dev.mad.ussd4etecsa.updateSaldo.Voz;
import dev.mad.ussd4etecsa.utiles.Util;

/**
 * Created by Daymel on 25-Apr-17.
 */

public class Accesibilidad extends AccessibilityService {

    public static String TAG = Accesibilidad.class.getSimpleName();
    DatabaseHelper dbHelper;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "Estoy en Accesibilidad");


        AccessibilityNodeInfo source = event.getSource();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event.getClassName().equals("android.app.AlertDialog")) {
            // android.app.AlertDialog is the standard but not for all phones

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !String.valueOf(event.getClassName()).contains("AlertDialog")) {
                return;
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.TextView"))) {
                return;
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && TextUtils.isEmpty(source.getText())) {
                return;
            }

            List<CharSequence> eventText;

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                eventText = event.getText();
            } else {
                eventText = Collections.singletonList(source.getText());
            }

            String text = processUSSDText(eventText);

            if (TextUtils.isEmpty(text)) return;

            // Close dialog
            if (!text.equals(getString(R.string.alert_dialog_title))) {
                performGlobalAction(GLOBAL_ACTION_BACK); // This works on 4.1+ only
            }

            Log.d(TAG, text);

            try {
                procesarRespuesta(text);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Handle USSD response here

        }
    }

    private void procesarRespuesta(String respuesta) throws SQLException {
        String test = "Bono->vence: $64.04->04-11-20. Datos 1.872 MB->04-11-20. MIN 00:46:11->04-11-20. SMS 50->04-11-20. Datos.cu 225 MB->04-11-20";
        SharedPreferences sharedPreferences = getSharedPreferences("ussdPreferences", Context.MODE_PRIVATE);
        String option = sharedPreferences.getString("refresh", "");
        List<String> valores = convertirCadena(respuesta);

        switch (option) {
            case "SALDO": {
                Saldo saldo = new Saldo(this);
                saldo.UpdateSaldo(valores);
                break;
            }
            case "VOZ": {
                Voz voz = new Voz(this);
                voz.UpdateSaldo(valores);
                break;
            }
            case "SMS": {
                Sms sms = new Sms(this);
                sms.UpdateSaldo(valores);
                break;
            }
            case "DATOS": {
                Datos datos = new Datos(this);
                datos.UpdateSaldo(valores);
                break;
            }
            case "BONO": {
                Bono bono = new Bono(this);
                bono.UpdateSaldo(valores);
                break;
            }
            default: {
                mostrarToast(respuesta);
            }
        }
    }


    private List<String> convertirCadena(String text) {
        List<String> valores = new ArrayList<>();
        StringTokenizer tProcesar = new StringTokenizer(text);
        while (tProcesar.hasMoreTokens()) {
            valores.add(tProcesar.nextToken());
        }
        return valores;
    }

    private String processUSSDText(List<CharSequence> eventText) {
        String s;
        if (eventText.size() == 3) {
            s = String.valueOf(eventText.get(1));
        } else {
            s = String.valueOf(eventText.get(0));
        }
        return s;
    }

    private void mostrarToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

}


