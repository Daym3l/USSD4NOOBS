package dev.mad.ussd4etecsa.services;

import android.content.Context;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.service.quicksettings.TileService;
import android.telephony.TelephonyManager;


import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

import dev.mad.ussd4etecsa.R;


@RequiresApi(api = Build.VERSION_CODES.N)
public class NetworkChangeTileService extends TileService {

    private static final int HPLUS = R.drawable.hplus;
    private static final int LTE = R.drawable.lte;
    private static final int NO = R.drawable.ic_splash;
    private static final String TAG = "net1";


    @Override
    public void onClick() {
        if (!getPreferredNetwork().equals("no")) {
            Intent myIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getApplicationContext().sendBroadcast(closeIntent);
            startActivity(myIntent);

        }
    }

    @Override
    public void onStartListening() {
        String type = getPreferredNetwork();
        Icon icon = Icon.createWithResource(getApplicationContext(), LTE);
        switch (type) {
            case "3g": {
                icon = Icon.createWithResource(getApplicationContext(), HPLUS);
                getQsTile().setLabel(getString(R.string.tile_name));
                break;
            }
            case "4g": {
                icon = Icon.createWithResource(getApplicationContext(), LTE);
                getQsTile().setLabel(getString(R.string.tile_name));
                break;
            }
            default: {
                icon = Icon.createWithResource(getApplicationContext(), NO);
                getQsTile().setLabel(getString(R.string.tile_disconected));
            }
        }
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }

    public String getPreferredNetwork() {
        String ntype = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable() || !info.getTypeName().equals("MOBILE")) {
            ntype = "no";
        } else {
            if (info.getSubtypeName().equals("HSDPA") || info.getSubtypeName().equals("UMTS")) {
                ntype = "3g";
            } else if (info.getSubtypeName().equals("HSPA+")) {
                ntype = "3g";
            } else {
                ntype = "4g";
            }
        }
        return ntype;
    }


    /**
     * Get a hidden method instance from a class
     *
     * @param methodName The name of the method to be taken from the class
     * @param fromClass  The name of the class that has the method
     * @return A Method instance that can be invoked
     */
    public Method getHiddenMethod(String methodName, Class fromClass, Class[] params) {
        Method method = null;
        try {
            Class clazz = Class.forName(fromClass.getName());
            method = clazz.getMethod(methodName, params);
            method.setAccessible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return method;
    }
}

