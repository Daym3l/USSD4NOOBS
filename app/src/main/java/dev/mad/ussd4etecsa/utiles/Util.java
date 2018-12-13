package dev.mad.ussd4etecsa.utiles;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by Daymel on 7/12/2017.
 */

public class Util {

    public static List<String> convertirCadena(String text) {
        List<String> valores = new ArrayList<>();
        StringTokenizer tProcesar = new StringTokenizer(text);
        while (tProcesar.hasMoreTokens()) {
            valores.add(tProcesar.nextToken());
        }
        return valores;
    }

    public static boolean verificarCampos(EditText campo) {

        if (TextUtils.isEmpty(campo.getText().toString())) {
            campo.setError(Html.fromHtml("<font color='red'>El campo es obligatorio</font>"));
            campo.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * get Contact ID
     *
     * @param contactNumber
     * @param context
     * @return
     */
    public static long getContactIDFromNumber(String contactNumber, Context context) {
        String UriContactNumber = Uri.encode(contactNumber);
        long phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, UriContactNumber),
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }

    /**
     * Return photo
     *
     * @param contactId
     * @param context
     * @return
     */
    public static Bitmap openPhoto(long contactId, Context context) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }

    /**
     * @param number
     * @return
     */
    public static String convertNumber(String number) {

        String[] parts = number.split("\\+");
        if (parts.length > 1)
            number = parts[1];
        return number;
    }

    public static void marcarNumero(String codigo, Activity activity) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        activity.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }

    /**
     * Obtener Tamaño de pantalla
     *
     * @param activity
     * @return
     */
    public static int getWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    /**
     * Obtener radio del circulo
     *
     * @param width
     * @return
     */
    public static int getDiameterCircle(int width) {
        int size = 0;
        switch (width) {
            case 720: {
                size = 60;
                break;
            }
            case 1080: {
                size = 70;
                break;
            }
            case 1440: {
                size = 80;
                break;
            }
            case 540: {
                size = 35;
                break;
            }
            case 480: {
                size = 35;
                break;
            }
        }
        return size;
    }

    /**
     * Cacular margen
     *
     * @param width
     * @return
     */
    public static int getTopMargin(int width) {
        int size = 0;
        switch (width) {
            case 720: {
                size = 50;
                break;
            }
            case 1080: {
                size = 80;
                break;
            }
            case 1440: {
                size = 90;
                break;
            }
            case 540: {
                size = 40;
                break;
            }
            case 480: {
                size = 35;
                break;
            }
        }
        return size;
    }

}
