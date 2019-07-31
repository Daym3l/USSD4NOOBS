package dev.mad.ussd4etecsa.transferencia;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.mad.ussd4etecsa.models.TransferenciaModel;
import dev.mad.ussd4etecsa.models.Tables.DatTranferencia;
import dev.mad.ussd4etecsa.R;

/**
 * Created by Daymel on 8/11/2017.
 */

public class AdapterTransferencia extends RecyclerView.Adapter<AdapterTransferencia.ViewHolder> {


    Context mcontext;
    TransferenciaModel transferenciaModel;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView numTelf;
        TextView fecha;
        TextView dinero;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            nombre = (TextView) view.findViewById(R.id.tv_nombre);
            numTelf = (TextView) view.findViewById(R.id.tv_numero);
            fecha = (TextView) view.findViewById(R.id.tv_fecha);
            dinero = (TextView) view.findViewById(R.id.tv_money);


        }
    }

    public AdapterTransferencia(Context context) {
        this.mcontext = context;
        this.transferenciaModel = new TransferenciaModel(context);
    }

    @Override
    public AdapterTransferencia.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tranferencia, parent, false);
        return new AdapterTransferencia.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            List<DatTranferencia> tranferencias = transferenciaModel.getTranferencias();
            DatTranferencia tranferencia = tranferencias.get(position);
            if (getItemCount() > 0) {
                holder.numTelf.setText(tranferencia.getNumeroContacto());
                holder.nombre.setText(getContactDisplayNameByNumber(tranferencia.getNumeroContacto()));
                holder.fecha.setText(tranferencia.getFechaTransferencia());
                holder.fecha.setText(tranferencia.getFechaTransferencia());
                holder.dinero.setText("$"+tranferencia.getValorTransferido());


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        List<DatTranferencia> cant = new ArrayList<DatTranferencia>();
        try {
            cant = transferenciaModel.getTranferencias();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cant.size();
    }

    /**
     *
     * @param number
     * @return
     */
    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "No registrado";

        ContentResolver contentResolver = mcontext.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
