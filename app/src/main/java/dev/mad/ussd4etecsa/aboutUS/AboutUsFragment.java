package dev.mad.ussd4etecsa.aboutUS;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dev.mad.ussd4etecsa.BuildConfig;
import dev.mad.ussd4etecsa.Nav_Principal;
import dev.mad.ussd4etecsa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    final static String VERSION = BuildConfig.VERSION_NAME;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_us, container, false);
        TextView version = (TextView) v.findViewById(R.id.tv_version_val);
        FloatingActionButton donar = (FloatingActionButton) v.findViewById(R.id.btn_donar);
        ImageView image = (ImageView) v.findViewById(R.id.iv_text_us);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });
        version.setText(VERSION);
        SharedPreferences prefs = getActivity().getSharedPreferences("ussdPreferences", getContext().MODE_PRIVATE);
        final String pass = prefs.getString("pass", "");
        ((Nav_Principal) getActivity()).getSupportActionBar().setTitle(R.string.about);

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder winDialog = new AlertDialog.Builder(getActivity());
                winDialog.setTitle("Donar!!!!");
                winDialog.setMessage("Que tal una cerveza...");
                winDialog.setPositiveButton("Cerveza", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marcarNumero("234*1*52832278*" + pass + "*1");
                    }
                });
                winDialog.setNegativeButton("No me interesa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                winDialog.show();
            }
        });
        return v;
    }

    /**
     * Intent para enviar correo
     */
    public void openEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"daym3l@nauta.cu"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Reportando bug ");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Breve explicación del error...");
        startActivity(Intent.createChooser(emailIntent, "Seleccione aplicación de correo"));

    }

    private void marcarNumero(String codigo) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }


}
