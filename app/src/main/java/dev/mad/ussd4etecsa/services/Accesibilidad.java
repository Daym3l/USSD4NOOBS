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

        List<String> valores = convertirCadena(respuesta);
        if (valores.size() > 6) {
            if (valores.get(0).equals("Saldo")) {
                updateSaldo(valores.get(1), valores.get(7), "SALDO");

            }
            if (valores.get(4).equals("MIN")) {
                updateSaldo(valores.get(3), valores.get(7), "VOZ");

            }
            if (valores.get(4).equals("SMS") && !valores.get(0).equals("Bono:Min.") && !valores.get(0).equals("Bono->vence:")) {
                updateSaldo(valores.get(3), valores.get(7), "SMS");

            }
            if ((valores.get(5).equals("KB")) || (valores.get(5).equals("MB")) || (valores.get(5).equals("GB"))) {
                updateSaldo(valores.get(4) + " " + valores.get(5), valores.get(7), "BOLSA");
            }
            if (((valores.get(4).equals("KB")) || (valores.get(4).equals("MB")) || (valores.get(4).equals("GB"))) && valores.size() > 7) {
                if (valores.get(2).equals("Paquetes:") ) {
                    if (valores.get(6).equals("KB") || valores.get(6).equals("MB") || valores.get(6).equals("GB")){
                        String texto = valores.get(3)+" "+valores.get(4)+" "+valores.get(5)+" "+valores.get(6)+" "+valores.get(7)+" "+valores.get(8);
                        updateSaldo(texto, valores.get(10), "BOLSA");
                    }else{
                        updateSaldo(valores.get(3) + " " + valores.get(4), valores.get(6), "BOLSA");
                    }

                } else {
                    updateSaldo(valores.get(3) + " " + valores.get(4), valores.get(7), "BOLSA");
                }
            }
            if (((valores.get(5).equals("KB")) || (valores.get(5).equals("MB")) || (valores.get(5).equals("GB"))) && valores.size() > 7) {
                if (valores.get(3).equals("Paquetes:") ) {
                    if (valores.get(7).equals("KB") || valores.get(7).equals("MB") || valores.get(7).equals("GB")){
                        String texto = valores.get(4)+" "+valores.get(5)+" "+valores.get(6)+" "+valores.get(7)+" "+valores.get(8)+" "+valores.get(9);
                        updateSaldo(texto, valores.get(11), "BOLSA");
                    }else{
                        updateSaldo(valores.get(4) + " " + valores.get(5), valores.get(7), "BOLSA");
                    }

                } else {
                    updateSaldo(valores.get(3) + " " + valores.get(4), valores.get(7), "BOLSA");
                }
            }

            if (valores.get(4).equals("MB.")) {
                updateSaldo("0" + " " + "MB", "0", "BOLSA");
            }
            if (valores.get(4).equals("Minutos")) {
                updateSaldo(valores.get(3), valores.get(7), "VOZ");
                Log.d("saldo", valores.get(3));
            }
            if (valores.get(6).equals("SMS.")) {
                updateSaldo("0", "0", "SMS");
            }
            if (valores.get(4).equals("Minutos") && valores.get(9).equals("nueva")) {
                updateSaldo("0:00:00 ", "0", "VOZ");
            }
            if (valores.get(4).equals("oferta.")) {
                updateSaldo("0.00", "0", "BOLSA");
            }

            if (valores.get(0).equals("Bono:Min.")) {
                String valor = valores.get(1);
                valor += " " + valores.get(5);
                updateSaldo(valor, valores.get(3), "BONO");
            }

            if (valores.get(4).equals("Amigos:")) {
                dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
                RuntimeExceptionDao<DatAmigo, Integer> datPlanAmigoses = this.dbHelper.getAmigoruntimeExceptionDao();
                DeleteBuilder<DatAmigo, Integer> deleteBuilder = datPlanAmigoses.deleteBuilder();
                deleteBuilder.delete();
                for (int i = 5; i < valores.size(); i++) {
                    DatAmigo amigo = new DatAmigo(valores.get(i));
                    datPlanAmigoses.create(amigo);
                }

            }
        }
        if (valores.size() == 9) {
            if (valores.get(8).equals("consultar.")) {
                SharedPreferences sharedPreferences = getSharedPreferences("ussdPreferences", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("amigos", false).commit();
            }
        }
        if (valores.size() == 6) {
            if (valores.get(4).equals("bonos")) {
                updateSaldo("00:00:00", "0-00-00", "BONO");
            }
        }

        if (valores.size() == 10) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = valores.get(3);
                    valor += " " + valores.get(7);
                    valor += " " + val;
                    updateSaldo(valor, valores.get(9), "BONO");
                }
            }
        }

        if (valores.size() == 10 && valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();

                if (bonos.equals("Bono")) {
                    String valor = "$"+ Util.getResultText(valores.get(2));
                    valor += " " +Util.getResultText(valores.get(4)) + " MIN " + Util.getResultText(valores.get(6)) + " SMS";
                    valor += " " + valores.get(8) + " MB ";
                    updateSaldo(valor, Util.getResultDate(valores.get(9)), "BONO");
                }
            }
        }
        if (valores.size() == 9 && valores.get(0).equals("Bono->vence:")) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "->");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();

                if (bonos.equals("Bono")) {
                    String valor =  Util.getResultText(valores.get(1));
                    valor += " " +Util.getResultText(valores.get(3)) + " MIN " + Util.getResultText(valores.get(5)) + " SMS";
                    valor += " " + valores.get(7) + " MB ";
                    updateSaldo(valor, Util.getResultDate(valores.get(8)), "BONO");
                }
            }
        }

        if (valores.size() == 7) {
            String bono = valores.get(0);
            String venceData = valores.get(2);
            String[] vence = venceData.split("\\.");
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = "$" + val;
                    valor += " " + valores.get(3);
                    valor += " " + valores.get(4);
                    updateSaldo(valor, vence[0], "BONO");
                }
            }
        }
        if (valores.size() == 14) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = "$" + val;
                    valor += " " + valores.get(3) + " MIN " + valores.get(7) + " SMS";
                    valor += " " + valores.get(10) + " " + valores.get(11);
                    updateSaldo(valor, valores.get(5), "BONO");
                }
            }
        }
        if (valores.size() == 11) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    String valor = "$" + val;
                    valor += " " + valores.get(4) + " SMS";
                    valor += " " + valores.get(7) + " " + valores.get(8);
                    updateSaldo(valor, valores.get(2), "BONO");
                }
            }
        }


        if (valores.size() == 3) {
            String bono = valores.get(0);
            StringTokenizer tokenizer = new StringTokenizer(bono, "$");
            if (tokenizer.countTokens() > 1) {
                String bonos = tokenizer.nextToken();
                String val = tokenizer.nextToken();
                if (bonos.equals("Bono:")) {
                    updateSaldo(val, valores.get(2), "BONO");
                }
            }
        }
        if (valores.size() == 5 && valores.get(0).equals("Bono:Datos.cu")) {
            String valor = valores.get(1);
            valor += valores.get(2);
            updateSaldo(valor, valores.get(4), "BONO");
        }
        if (!respuesta.equals(getString(R.string.alert_dialog_title))) {
            mostrarToast(respuesta);
        }


    }

    public void updateSaldo(String valor, String vence, String opcion) throws SQLException {

        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
        UpdateBuilder<DatUssd, Integer> updateBuilder = ussdDao.updateBuilder();
        valor = (valor.equals("de")) ? "0" : valor;
        vence = (vence.equals("plan.")) ? "0" : vence;
        vence = (vence.equals("hoy.")) ? "1" : vence;
        updateBuilder.where().eq("name", opcion);
        updateBuilder.updateColumnValue("valor", valor);
        updateBuilder.updateColumnValue("fechavencimiento", vence);
        updateBuilder.update();


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
        if(eventText.size()==3){
            s = String.valueOf(eventText.get(1));
        }else{
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


