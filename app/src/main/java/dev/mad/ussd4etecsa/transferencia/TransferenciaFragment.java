package dev.mad.ussd4etecsa.transferencia;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import dev.mad.ussd4etecsa.R;
import dev.mad.ussd4etecsa.utiles.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferenciaFragment extends Fragment {

    FloatingActionButton btnContacto;
    FloatingActionButton btnCambiarPass;
    FloatingActionButton btnTransferir;
    EditText numTelf;
    EditText pass;
    EditText dineroTrans;
    RecyclerView listaTrans;
    private SharedPreferences prefs;
    AdapterTransferencia adapterTransferencia;


    final static int PICK_CONTACT = 456;

    public TransferenciaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_transferencia, container, false);
        prefs = getActivity().getSharedPreferences("ussdPreferences", getContext().MODE_PRIVATE);

        btnContacto = (FloatingActionButton) v.findViewById(R.id.btn_transferir_contacto);
        btnCambiarPass = (FloatingActionButton) v.findViewById(R.id.btn_cambiar_contraseña);
        btnTransferir = (FloatingActionButton) v.findViewById(R.id.btn_tranfer);
        numTelf = (EditText) v.findViewById(R.id.et_numtelf);
        pass = (EditText) v.findViewById(R.id.et_contrasenna);
        dineroTrans = (EditText) v.findViewById(R.id.et_money);
        listaTrans = (RecyclerView) v.findViewById(R.id.rv_transferencia);
        pass.setText(prefs.getString("pass", ""));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listaTrans.setLayoutManager(linearLayoutManager);

        adapterTransferencia = new AdapterTransferencia(getContext());
        listaTrans.setAdapter(adapterTransferencia);


        btnContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        btnCambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarNumero("234*2");
            }
        });

        btnTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contacto = numTelf.getText().toString();
                String passw = pass.getText().toString();
                String moneyttrans = dineroTrans.getText().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("pass", passw);
                editor.commit();
                if (Util.verificarCampos(numTelf) && Util.verificarCampos(pass) && Util.verificarCampos(dineroTrans)) {
                    marcarNumero("234*1*" + contacto + "*" + passw + "*" + moneyttrans);
                    adapterTransferencia.notifyItemInserted(0);
                    listaTrans.scrollToPosition(0);
                    numTelf.setText("");
                    dineroTrans.setText("1");

                }

            }
        });
        return v;


    }

    /**
     * todo terminar lo de seleccionar el contacto
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT): {
                if (resultCode == getActivity().RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    cursor.moveToFirst();

                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    numTelf.setText(Util.convertNumber(number));

                }
            }
        }
    }



    /**
     * @param codigo
     */
    private void marcarNumero(String codigo) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }

    /**
     * @param campo
     * @return
     */


}
