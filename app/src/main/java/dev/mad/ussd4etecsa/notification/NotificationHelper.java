package dev.mad.ussd4etecsa.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.service.notification.StatusBarNotification;


import androidx.core.app.NotificationCompat;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.models.Tables.DatUssd;
import dev.mad.ussd4etecsa.Nav_Principal;
import dev.mad.ussd4etecsa.R;
import dev.mad.ussd4etecsa.utiles.Util;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Daymel on 9/3/2017.
 */

public class NotificationHelper {


    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

    static String servNotifications = NOTIFICATION_SERVICE;
    int iconNotificactionApp = R.drawable.ic_notification;
    NotificationChannel mChanel;
    NotificationManager notificationManager;
    CharSequence name = "ussd4noobs";
    String description = "Notification for Ussd4noobs";
    int importance = NotificationManager.IMPORTANCE_LOW;
    Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) this.context.getSystemService(servNotifications);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.mChanel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            this.mChanel.setDescription(description);
            this.mChanel.enableLights(true);
            this.mChanel.setLightColor(R.color.primary);
            this.mChanel.enableVibration(true);
            this.mChanel.setVibrationPattern(new long[]{0, 100, 100, 100, 100, 100});

            notificationManager.createNotificationChannel(mChanel);
        }
    }


    public void sendUpdateNotificacion() {
        if (verificarNotifications()) {
            resetNotication();
        } else {
            sendNotifacation();
        }
    }

    /**
     * Verificar q la notificacion este activa
     *
     * @return
     */

    private boolean verificarNotifications() {
        final StatusBarNotification[] activeNotifications = this.notificationManager.getActiveNotifications();
        boolean flag = false;
        for (StatusBarNotification notification : activeNotifications) {
            flag = notification.getId() == NOTIFICATION_ID;
        }
        return flag;
    }

    /**
     * Volver a crear la notificación
     */

    private void resetNotication() {
        notificationManager.cancel(NOTIFICATION_ID);
        sendNotifacation();
    }

    public void closeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }


    private void sendNotifacation() {


        long hora = System.currentTimeMillis();
        // Conexion a la BD
        DatabaseHelper dbHelper = OpenHelperManager.getHelper(this.context, DatabaseHelper.class);
        RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
        List<DatUssd> ussdObjetctSaldo = ussdDao.queryForEq("name", "SALDO");
        List<DatUssd> ussdObjetctVoz = ussdDao.queryForEq("name", "VOZ");
        List<DatUssd> ussdObjetctSms = ussdDao.queryForEq("name", "SMS");
        List<DatUssd> ussdObjetctBolsa = ussdDao.queryForEq("name", "BOLSA");
        List<DatUssd> ussdObjetctBono = ussdDao.queryForEq("name", "BONO");

        String saldos = "Saldo principal: " + ussdObjetctSaldo.get(0).getValor();

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        Notification.InboxStyle ninboxStyle = new Notification.InboxStyle();

        Notification.Builder nBuilder = null;
        NotificationCompat.Builder mBuilder = null;


        Intent notificationIntent;

        notificationIntent = new Intent(this.context, Nav_Principal.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= 26) {
            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setSmallIcon(iconNotificactionApp)
                    .setOngoing(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Saldo Disponible")
                    .setContentText(saldos);
        } else {
            nBuilder = new Notification.Builder(context);
            nBuilder.setContentIntent(contentIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setSmallIcon(iconNotificactionApp).setWhen(hora)
                    .setContentTitle("Saldo Disponible")
                    .setContentText(saldos);
        }


        if (!ussdObjetctVoz.get(0).getValor().equals("0") || !ussdObjetctSms.get(0).getValor().equals("0") || !ussdObjetctBolsa.get(0).getValor().equals("0")) {
            String[] events = new String[5];

            events[0] = saldos;
            if (!ussdObjetctVoz.get(0).getValor().equals("0") && !ussdObjetctVoz.get(0).getValor().equals("0:00:00")) {
                events[1] = "VOZ: " + ussdObjetctVoz.get(0).getValor() + " Min por " + ussdObjetctVoz.get(0).getFechavencimiento() + " días";
            }
            if (!ussdObjetctSms.get(0).getValor().equals("0")) {
                events[2] = "SMS: " + ussdObjetctSms.get(0).getValor() + " SMS por " + ussdObjetctSms.get(0).getFechavencimiento() + " días";
            }
            if (!ussdObjetctBolsa.get(0).getValor().equals("0") && !ussdObjetctBolsa.get(0).getValor().equals("0.00")) {
                events[3] = "DATOS: " + ussdObjetctBolsa.get(0).getValor() + " por " + ussdObjetctBolsa.get(0).getFechavencimiento() + " días";
            }
            if (!ussdObjetctBono.get(0).getValor().equals("00:00:00") && !ussdObjetctBono.get(0).getValor().equals("0.00")) {
                List bono = Util.convertirCadena(ussdObjetctBono.get(0).getValor());
                events[4] = "BONO: " + bono.get(0) + " MIN y " + bono.get(1) + " SMS hasta el " + ussdObjetctBono.get(0).getFechavencimiento();
            }


            inboxStyle.setBigContentTitle("Saldos Disponibles:");
            ninboxStyle.setBigContentTitle("Saldos Disponibles:");

            // Moves events into the big view
            for (int i = 0; i < events.length; i++) {
                inboxStyle.addLine(events[i]);
                ninboxStyle.addLine(events[i]);
            }
            if (mBuilder != null) {
                mBuilder.setStyle(inboxStyle);
            }
            if (nBuilder != null) {
                nBuilder.setStyle(ninboxStyle);
            }


        }
        Notification notification;
        if (Build.VERSION.SDK_INT >= 26) {

            notification = mBuilder.build();
        } else {

            notification = nBuilder.build();
        }


        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(NOTIFICATION_ID, notification);

    }
}
