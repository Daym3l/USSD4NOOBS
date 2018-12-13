package dev.mad.ussd4etecsa.amigos;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import dev.mad.ussd4etecsa.R;
import dev.mad.ussd4etecsa.utiles.Util;
import dev.mad.ussd4etecsa.models.AmigosModel;
import dev.mad.ussd4etecsa.models.Tables.DatAmigo;
import dev.mad.ussd4etecsa.utiles.RoundedImageView;

/**
 * Created by Daymel on 26/6/2018.
 */

public class AdapterAmigos extends RecyclerView.Adapter<AdapterAmigos.ViewHolder> {
    Context mcontext;
    AmigosModel amigosModel;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView contacto;
        TextView numero;
        TextView numeroContacto;
        ImageView eliminar;

        public ViewHolder(View itemView) {
            super(itemView);

            contacto = (RoundedImageView) itemView.findViewById(R.id.iv_amigo);
            numero = (TextView) itemView.findViewById(R.id.tv_name_amigo);
            numeroContacto = (TextView) itemView.findViewById(R.id.tv_numero);
            eliminar = (ImageView) itemView.findViewById(R.id.iv_eliminar_amigo);
        }
    }

    public AdapterAmigos(Context context) {
        this.mcontext = context;
        this.amigosModel = new AmigosModel(context);
    }

    @Override
    public AdapterAmigos.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amigo_list, parent, false);
        return new AdapterAmigos.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            List<DatAmigo> amigos = amigosModel.getAmigos();
            final DatAmigo amigo = amigos.get(position);
            if (getItemCount() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(amigo.getNumeroContacto(), ".");
                final String nContact = tokenizer.nextToken();
                holder.numero.setText(getContactDisplayNameByNumber(nContact));
                holder.numeroContacto.setText(nContact.substring(2, 10));
                holder.eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String val = nContact.substring(2, 10);
                        String uri = "133*4*2*2*" + val;
                        marcarNumero(uri);
                        AmigosModel amigosModel = new AmigosModel(mcontext);
                        try {
                            amigosModel.delAmigo(amigo.getId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        notifyItemRemoved(position);

                    }
                });
                holder.contacto.setImageBitmap(Util.openPhoto(Util.getContactIDFromNumber(nContact, mcontext), mcontext));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        List<DatAmigo> amigos = new ArrayList<>();
        try {
            amigos = amigosModel.getAmigos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return amigos.size();
    }

    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "No registrado";

        ContentResolver contentResolver = mcontext.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    private void marcarNumero(String codigo) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        mcontext.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }


}
