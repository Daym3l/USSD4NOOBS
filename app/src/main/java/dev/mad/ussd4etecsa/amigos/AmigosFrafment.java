package dev.mad.ussd4etecsa.amigos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.R;
import dev.mad.ussd4etecsa.models.AmigosModel;
import dev.mad.ussd4etecsa.models.Tables.DatAmigo;
import dev.mad.ussd4etecsa.utiles.Util;
import me.drakeet.materialdialog.MaterialDialog;
// TODO: 9/8/2018  arreglar la pantalla para dispositivos peque;os 

public class AmigosFrafment extends Fragment {

    private SharedPreferences sharedPreferences;

    private Switch plan;

    private FloatingActionButton refreshFriends, addFriend;
    RecyclerView rv_listado;
    AdapterAmigos adapterAmigos;
    final static int PICK_CONTACT = 456;
    AmigosModel amigosModel;


    public AmigosFrafment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_amigos_frafment, container, false);
        sharedPreferences = getActivity().getSharedPreferences("ussdPreferences", Context.MODE_PRIVATE);
        amigosModel = new AmigosModel(getContext());
        plan = (Switch) v.findViewById(R.id.sw_amigos);
        addFriend = (FloatingActionButton) v.findViewById(R.id.fabadd);
        refreshFriends = (FloatingActionButton) v.findViewById(R.id.fabRefresch);
        rv_listado = (RecyclerView) v.findViewById(R.id.rv_listado);


        try {
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listado.setLayoutManager(linearLayoutManager);

        adapterAmigos = new AdapterAmigos(getContext());
        rv_listado.setAdapter(adapterAmigos);
        refreshFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.marcarNumero("133*4*3*1*1", getActivity());
                try {
                    List<DatAmigo> amigos = amigosModel.getAmigos();
                    if (amigos.size() < 3) {
                        addFriend.setEnabled(true);
                    } else {
                        addFriend.setEnabled(false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        plan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Util.marcarNumero("133*4*1*1", getActivity());


                } else {
                    final MaterialDialog materialDialog = new MaterialDialog(getContext());
                    materialDialog.setTitle("Desactivar");
                    materialDialog.setMessage("Está seguro que desea desactivar el plan de amigos.");
                    materialDialog.setNegativeButton("Cancelar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                            plan.setChecked(true);

                        }
                    });
                    materialDialog.setPositiveButton("Aceptar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.marcarNumero("133*4*1*2", getActivity());
                            addFriend.setEnabled(false);
                            refreshFriends.setEnabled(false);
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.show();

                }
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });


        return v;
    }

    private void init() throws SQLException {

        List<DatAmigo> amigos = amigosModel.getAmigos();

        if (amigos.size() > 0) {
            //Actualizar vista
            sharedPreferences.edit().putBoolean("amigos", true).commit();
            plan.setChecked(true);
            refreshFriends.setEnabled(true);
            if (amigos.size() < 3) {
                addFriend.setEnabled(true);
                plan.setChecked(true);
            }
        } else {
            if (plan.isChecked()) {
                Util.marcarNumero("133*4*3*1*1", getActivity());
            } else {
                addFriend.setEnabled(false);
                refreshFriends.setEnabled(false);
            }


        }


    }


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
                    String num = Util.convertNumber(number);
                    Util.marcarNumero("133*4*2*1*" + num, getActivity());
                }
            }
        }
    }

}
