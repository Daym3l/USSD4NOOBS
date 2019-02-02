package dev.mad.ussd4etecsa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import net.frederico.showtipsview.ShowTipsBuilder;
import net.frederico.showtipsview.ShowTipsView;
import net.frederico.showtipsview.ShowTipsViewInterface;

import java.util.List;

import dev.mad.ussd4etecsa.aboutUS.AboutUsFragment;
import dev.mad.ussd4etecsa.amigos.AmigosFrafment;
import dev.mad.ussd4etecsa.settings.ConfigActivity;
import dev.mad.ussd4etecsa.bd_config.DatabaseHelper;
import dev.mad.ussd4etecsa.models.Tables.DatUssd;
import dev.mad.ussd4etecsa.notification.NotificationHelper;
import dev.mad.ussd4etecsa.services.Accesibilidad;
import dev.mad.ussd4etecsa.services.GeneralService;
import dev.mad.ussd4etecsa.services.UssdService;
import dev.mad.ussd4etecsa.transferencia.TransferenciaFragment;
import dev.mad.ussd4etecsa.utiles.Constantes;
import dev.mad.ussd4etecsa.utiles.Util;


public class Nav_Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageButton refresh;
    ImageView bolsa;
    ImageView sms;
    ImageView voz;
    ImageView iv_bono;

    TextView descripcion;
    TextView tipoRed;
    TextView proveedor;
    TextView saldo;
    TextView venceSaldo;
    TextView bono;
    TextView bonoSms;
    TextView venceBono;
    TextView tv_voz;
    TextView tv_vozVence;
    TextView tv_sms;
    TextView tv_smsVence;
    TextView tv_bolsa;
    TextView tv_bolsaVence;
    TextView tv_activo_Datos;
    TextView tv_activo_Voz;
    TextView tv_activo_Sms;
    TextView tv_activo_Bono;
    CardView cv_bono;
    EditText alertText;
    FragmentManager fragmentManager;
    CollapsingToolbarLayout collapsingToolbarLayout;
    NotificationHelper notificationHelper;
    DatabaseHelper dbHelper;
    ScrollView scrollView;
    Toolbar toolbar;
    Intent intentMemoryService;
    ShowTipsView t_inicial;
    int p_size;
    SharedPreferences sharedPreferences;
    private static final String TAG_ABOUT = "ABOUT";
    private static final String TAG_TRANFERENCIA = "TRANSFERENCIAS";
    private static final String TAG_AMIGOS = "AMIGOS";

    private static final String[] ARRAY_VOZ = {"5 Minutos / $1.50", "10 Minutos / $2.90", "15 Minutos / $4.20", "25 Minutos / $6.50", "40 Minutos / $10.00"};
    private static final String[] ARRAY_SMS = {"10 Mensajes / $0.70", "20 Mensajes / $1.30", "35 Mensajes / $2.10", "45 Mensajes / $2.45"};
    private static final String[] ARRAY_DATOS = {"Bolsa Nauta", "Tarifa por Consumo","600MB de Internet / $7"," 1GB de Internet/ $10","2.5GB de Internet / $20","4GB de Internet / $30"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("ussdPreferences", Context.MODE_PRIVATE);
        p_size = Util.getWidth(this);
        fragmentManager = getSupportFragmentManager();
        notificationHelper = new NotificationHelper(getApplicationContext());
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.maincollapsing);
        cv_bono = (CardView) findViewById(R.id.cv_bono);
        cv_bono = (CardView) findViewById(R.id.cv_saldo);
        refresh = (ImageButton) findViewById(R.id.btn_refresh);
        saldo = (TextView) findViewById(R.id.tv_valor_saldo);
        venceSaldo = (TextView) findViewById(R.id.tv_valor_vence);
        bono = (TextView) findViewById(R.id.tv_bono_saldo);
        bonoSms = (TextView) findViewById(R.id.tv_bono_sms);
        scrollView = (ScrollView) findViewById(R.id.sv_contenedor);
        venceBono = (TextView) findViewById(R.id.tv_bono_vence);
        tv_voz = (TextView) findViewById(R.id.tv_voz_vaue);
        tv_vozVence = (TextView) findViewById(R.id.tv_vtime_val);

        tv_activo_Datos = (TextView) findViewById(R.id.tv_act_datos);
        tv_activo_Sms = (TextView) findViewById(R.id.tv_act_sms);
        tv_activo_Voz = (TextView) findViewById(R.id.tv_act_voz);
        tv_activo_Bono = (TextView) findViewById(R.id.tv_noservice_bono);

        tv_sms = (TextView) findViewById(R.id.tv_sms_val);
        tv_smsVence = (TextView) findViewById(R.id.tv_stime_val);
        alertText = (EditText) findViewById(R.id.et_codRecarga);
        alertText.clearFocus();
        tv_bolsa = (TextView) findViewById(R.id.tv_bolsa_value);
        tv_bolsaVence = (TextView) findViewById(R.id.tv_btime_val);
        FloatingActionButton recargarSaldo = (FloatingActionButton) findViewById(R.id.btn_transferir_contacto);
        bolsa = (ImageView) findViewById(R.id.iv_bolsa);
        sms = (ImageView) findViewById(R.id.iv_sms);
        voz = (ImageView) findViewById(R.id.iv_mic);
        iv_bono = (ImageView) findViewById(R.id.iv_bono);
        descripcion = (TextView) findViewById(R.id.tv_descrip);
        tipoRed = (TextView) findViewById(R.id.tv_tipodred);
        proveedor = (TextView) findViewById(R.id.tv_proveedor_name);
        intentMemoryService = new Intent(getApplicationContext(), UssdService.class);
        startService(new Intent(getApplicationContext(), GeneralService.class));

        /*
        Tutorial
         */

        if (sharedPreferences.contains("ayuda_principal")) {
            boolean isShow = sharedPreferences.getBoolean("ayuda_principal", false);
            if (!isShow) {
                helpApp();
            }
        } else {
            helpApp();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constantes.ACTION_RUN_SERVICE);
        filter.addAction(Constantes.ACTION_USSD_EXIT);

        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        recargarSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText alertText = (EditText) findViewById(R.id.et_codRecarga);
                Editable YouEditTextValue = alertText.getText();
                if (verificarCampos(alertText)) {
                    marcarNumero("662*" + String.valueOf(YouEditTextValue));
                    alertText.setText("");
                }

            }
        });
        //Boton refrescar
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222");

            }
        });

        //Imagen de la bolsa
        bolsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222*328");

            }
        });

        //imagen voz
        voz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222*869");

            }
        });
        iv_bono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222*266");
            }
        });

        //imagen png_sms
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222*767");

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void helpApp() {


        t_inicial = new ShowTipsBuilder(this)
                .setTarget(toolbar, (Util.getWidth(Nav_Principal.this) - (Util.getDiameterCircle(p_size) + Util.getDiameterCircle(p_size) / 2)) - Util.getDiameterCircle(p_size), Util.getTopMargin(p_size), Util.getDiameterCircle(p_size))
                .setCloseButtonColor(Color.parseColor("#3f51b5"))
                .setTitle(getString(R.string.t_inicio))
                .setTitleColor(Color.parseColor("#00BCD4"))
                .setDescription(getString(R.string.t_descripcion))
                .setDelay(900)

                .setButtonText(getString(R.string.t_btn_continuar))
                .build();
        t_inicial.show(this);


        t_inicial.setCallback(new ShowTipsViewInterface() {
            @Override
            public void gotItClicked() {

                t_inicial = new ShowTipsBuilder(Nav_Principal.this)
                        .setTarget(refresh)
                        .setCloseButtonColor(Color.parseColor("#3f51b5"))
                        .setTitle(getString(R.string.t_saldo))
                        .setTitleColor(Color.parseColor("#00BCD4"))
                        .setDescription(getString(R.string.t_saldo_descripcion))
                        .setDelay(500)

                        .setButtonText(getString(R.string.t_btn_continuar))
                        .build();
                t_inicial.show(Nav_Principal.this);
                t_inicial.setCallback(new ShowTipsViewInterface() {
                    @Override
                    public void gotItClicked() {
                        t_inicial = new ShowTipsBuilder(Nav_Principal.this)
                                .setTarget(findViewById(R.id.iv_config_voz))
                                .setCloseButtonColor(Color.parseColor("#3f51b5"))
                                .setTitle(getString(R.string.t_planes))
                                .setTitleColor(Color.parseColor("#00BCD4"))
                                .setDescription(getString(R.string.t_planes_descripcion))
                                .setDelay(500)

                                .setButtonText(getString(R.string.t_btn_continuar))
                                .build();
                        t_inicial.show(Nav_Principal.this);
                        t_inicial.setCallback(new ShowTipsViewInterface() {
                            @Override
                            public void gotItClicked() {
                                t_inicial = new ShowTipsBuilder(Nav_Principal.this)
                                        .setTarget(voz)
                                        .setCloseButtonColor(Color.parseColor("#3f51b5"))
                                        .setTitle(getString(R.string.t_planes))
                                        .setTitleColor(Color.parseColor("#00BCD4"))
                                        .setDescription(getString(R.string.t_planes_update))
                                        .setDelay(500)

                                        .setButtonText(getString(R.string.t_btn_terminar))
                                        .build();
                                t_inicial.show(Nav_Principal.this);
                                t_inicial.setCallback(new ShowTipsViewInterface() {
                                    @Override
                                    public void gotItClicked() {
                                        sharedPreferences.edit().putBoolean("ayuda_principal", true).apply();
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        stopService(intentMemoryService);
        notificationHelper.sendUpdateNotificacion();
    }

    protected void onStart() {
        super.onStart();
        startService(intentMemoryService);
        notificationHelper.sendUpdateNotificacion();


    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentManager.getBackStackEntryCount() == 1) {
                collapsingToolbarLayout.setVisibility(View.VISIBLE);
                toolbar.setTitle(getString(R.string.app_name));
            }

            super.onBackPressed();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_help: {
                fragmentGestor(new AboutUsFragment(), TAG_ABOUT);
                return true;
            }
            case R.id.action_config: {
                getConfig();
                return true;
            }

        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Función para pintar los fragmentos en la vista principal
     *
     * @param fragment
     */
    private void fragmentGestor(Fragment fragment, String tag) {
        collapsingToolbarLayout.setVisibility(View.GONE);
        Fragment containerFragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (containerFragment == null) {
            fragmentTransaction.replace(R.id.cordinador, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        } else if (!tag.equals(containerFragment.getTag()) || tag.equals(containerFragment.getTag())) {
            fragmentTransaction.replace(R.id.cordinador, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();

        }

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
     *
     */
    private void getConfig() {
        Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.nav_configuracion) {
            getConfig();
            return true;

        } else if (id == R.id.nav_transferir) {
            fragmentGestor(new TransferenciaFragment(), TAG_TRANFERENCIA);
            toolbar.setTitle(getString(R.string.transferir));


        } else if (id == R.id.nav_share) {

            fragmentGestor(new AboutUsFragment(), TAG_ABOUT);


        } else if (id == R.id.nav_amigos) {
            fragmentGestor(new AmigosFrafment(), TAG_AMIGOS);
            toolbar.setTitle(getString(R.string.amigos));
        } else if (id == R.id.nav_help) {
            helpApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Mostrar mensajes al usuarios
     *
     * @param msg
     */
    private void mostrarMensaje(String msg) {
        Snackbar.make(findViewById(R.id.cordinador), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Manejador de dialogos
     */
    public void mostrarDialogos(View v) {
        final String[] valor = {"0"};
        final AlertDialog.Builder winDialog = new AlertDialog.Builder(this);
        switch (v.getId()) {
            case R.id.iv_config_voz: {

                winDialog.setTitle(getString(R.string.Principal_voz)).setSingleChoiceItems(ARRAY_VOZ, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                valor[0] = "133*3*1";
                                break;
                            }
                            case 1: {
                                valor[0] = "133*3*2";
                                break;
                            }
                            case 2: {
                                valor[0] = "133*3*3";
                                break;
                            }
                            case 3: {
                                valor[0] = "133*3*4";
                                break;
                            }
                            case 4: {
                                valor[0] = "133*3*5";
                                break;
                            }
                        }
                    }
                });
                winDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (valor[0].equals("0")) {
                            mostrarMensaje(getString(R.string.Principal_seleccionar));
                        } else {
                            marcarNumero(valor[0]);
                            dialog.dismiss();
                        }

                    }
                });
                winDialog.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        marcarNumero("222*869");
                        dialog.dismiss();
                    }
                });

                break;
            }

            case R.id.iv_config_sms: {

                winDialog.setTitle(getString(R.string.Principal_sms)).setSingleChoiceItems(ARRAY_SMS, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                valor[0] = "133*2*1";
                                break;
                            }
                            case 1: {
                                valor[0] = "133*2*2";
                                break;
                            }
                            case 2: {
                                valor[0] = "133*2*3";
                                break;
                            }
                            case 3: {
                                valor[0] = "133*2*4";
                                break;
                            }

                        }
                    }
                });
                winDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (valor[0].equals("0")) {
                            mostrarMensaje(getString(R.string.Principal_seleccionar));

                        } else {
                            marcarNumero(valor[0]);
                            dialog.dismiss();
                        }
                    }
                });
                winDialog.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        marcarNumero("222*767");
                        dialog.dismiss();
                    }
                });

                break;
            }

            case R.id.iv_config_datos: {

                winDialog.setTitle(getString(R.string.Principal_datos)).setSingleChoiceItems(ARRAY_DATOS, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                valor[0] = "133*1*2";
                                break;
                            }
                            case 1: {
                                valor[0] = "133*1*1";
                                break;
                            }
                            case 2: {
                                valor[0] = "133*1*3*1";
                                break;
                            }
                            case 3: {
                                valor[0] = "133*1*3*2";
                                break;
                            }
                            case 4: {
                                valor[0] = "133*1*3*3";
                                break;
                            }
                            case 5: {
                                valor[0] = "133*1*3*4";
                                break;
                            }


                        }
                    }
                });
                winDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (valor[0].equals("0")) {
                            mostrarMensaje(getString(R.string.Principal_seleccionar));

                        } else {
                            marcarNumero(valor[0]);
                            dialog.dismiss();
                        }
                    }
                });
                winDialog.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        marcarNumero("222*328");
                        dialog.dismiss();
                    }
                });

                break;
            }

        }
        AlertDialog dialog = winDialog.create();
        dialog.show();
    }

    /**
     * Marcar Codigo USSD
     *
     * @param codigo
     */
    private void marcarNumero(String codigo) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }

    // Broadcast receiver que recibe las emisiones desde los servicios
    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constantes.ACTION_RUN_SERVICE:
                    refrescarValores(context);
                    getDatosRed(context);

                    break;

                case Constantes.ACTION_USSD_EXIT:
                    refrescarValores(context);
                    break;

            }
        }

        /**
         * Obtiene los png_datos de la red movil
         *
         * @param context
         */
        private void getDatosRed(Context context) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            proveedor.setText(manager.getNetworkOperatorName());
            if (info == null || !info.isConnected() || !info.isAvailable() || !info.getTypeName().equals("MOBILE")) {
                descripcion.setText("APN");
                tipoRed.setText("tipo de red");

            } else {
                descripcion.setText(info.getExtraInfo());


                if (info.getSubtypeName().equals("HSDPA") || info.getSubtypeName().equals("UMTS")) {

                    tipoRed.setText(info.getTypeName() + " " + "3G");
                } else if (info.getSubtypeName().equals("HSPA+")) {
                    tipoRed.setText(info.getTypeName() + " " + "3G Plus");
                } else if (info.getSubtypeName().equals("EDGE")) {
                    tipoRed.setText(info.getTypeName() + " " + "2G");
                } else {
                    tipoRed.setText(info.getTypeName() + " " + info.getSubtypeName());
                }
            }

        }

        /**
         * Refresaca los valores en la actividad
         *
         * @param context
         */
        public void refrescarValores(Context context) {
            dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
            List<DatUssd> ussdObjetctSaldo = ussdDao.queryForEq("name", "SALDO");
            List<DatUssd> ussdObjetctBono = ussdDao.queryForEq("name", "BONO");
            saldo.setText(ussdObjetctSaldo.get(0).getValor());
            venceSaldo.setText(ussdObjetctSaldo.get(0).getFechavencimiento() + " ");


            // Servicio de Bono
            if (ussdObjetctBono.get(0).getValor().equals("00:00:00") || ussdObjetctBono.get(0).getValor().equals("0.00")) {
                bono.setText(ussdObjetctBono.get(0).getValor() + " MIN");
                bonoSms.setText(0 + " SMS");
                venceBono.setText(ussdObjetctBono.get(0).getFechavencimiento());
                tv_activo_Bono.setText("Sin Bonificación.");
                tv_activo_Bono.setTextColor(getResources().getColor(R.color.rojo));
            } else {
                List<String> valores = Util.convertirCadena(ussdObjetctBono.get(0).getValor());
                if (valores.size() == 2) {
                    bono.setText(valores.get(0) + " MIN");
                    bonoSms.setText(valores.get(1) + " SMS");
                    tv_activo_Bono.setText("Bono activo");
                } else if (valores.size() == 3) {
                    bono.setText(valores.get(0) + " MIN");
                    bonoSms.setText(valores.get(1) + " SMS");
                    tv_activo_Bono.setText("$" + valores.get(2));
                } else {
                    tv_activo_Bono.setText("$" + valores.get(0));
                }

                venceBono.setText(ussdObjetctBono.get(0).getFechavencimiento());
                tv_activo_Bono.setTextColor(getResources().getColor(R.color.verde));

            }

            // Servicio de VOZ
            List<DatUssd> ussdObjetctVoz = ussdDao.queryForEq("name", "VOZ");
            if (ussdObjetctVoz.get(0).getFechavencimiento().equals("0")) {
                tv_activo_Voz.setText("Sin servicio.");
                tv_activo_Voz.setTextColor(getResources().getColor(R.color.rojo));
                tv_voz.setText(ussdObjetctVoz.get(0).getValor() + " MIN");
                tv_vozVence.setText(ussdObjetctVoz.get(0).getFechavencimiento() + " días");
            } else {
                tv_activo_Voz.setText("Servicio activo.");
                tv_activo_Voz.setTextColor(getResources().getColor(R.color.verde));
                tv_voz.setText(ussdObjetctVoz.get(0).getValor() + " MIN");
                tv_vozVence.setText(ussdObjetctVoz.get(0).getFechavencimiento() + " días");
            }

            // Servicio de SMS
            List<DatUssd> ussdObjetctSms = ussdDao.queryForEq("name", "SMS");
            if (ussdObjetctSms.get(0).getFechavencimiento().equals("0")) {
                tv_activo_Sms.setText("Sin servicio.");
                tv_activo_Sms.setTextColor(getResources().getColor(R.color.rojo));
                tv_sms.setText(ussdObjetctSms.get(0).getValor() + " SMS");
                tv_smsVence.setText(ussdObjetctSms.get(0).getFechavencimiento() + " días");
            } else {
                tv_activo_Sms.setText("Servicio activo.");
                tv_activo_Sms.setTextColor(getResources().getColor(R.color.verde));
                tv_sms.setText(ussdObjetctSms.get(0).getValor() + " SMS");
                tv_smsVence.setText(ussdObjetctSms.get(0).getFechavencimiento() + " días");
            }

            // Servicio de BOLSA
            List<DatUssd> ussdObjetctBolsa = ussdDao.queryForEq("name", "BOLSA");
            if (ussdObjetctBolsa.get(0).getFechavencimiento().equals("0")) {
                tv_activo_Datos.setText("Sin servicio.");
                tv_activo_Datos.setTextColor(getResources().getColor(R.color.rojo));
                tv_bolsa.setText(ussdObjetctBolsa.get(0).getValor());
                tv_bolsaVence.setText(ussdObjetctBolsa.get(0).getFechavencimiento() + " días");
            } else {
                tv_activo_Datos.setText("Servicio activo.");
                tv_activo_Datos.setTextColor(getResources().getColor(R.color.verde));
                tv_bolsa.setText(ussdObjetctBolsa.get(0).getValor());
                tv_bolsaVence.setText(ussdObjetctBolsa.get(0).getFechavencimiento() + " días");
            }

        }
    }
}
