package com.exitae.enric.trabajo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import java.util.Locale;


public class PrincipalActivity extends Activity implements View.OnClickListener{

    private static final int DIALOG_ABOUT = 0;

    private static final int RESULT_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setTitle(R.string.menuTitulo);

        // No mostrar el actionBar
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();

        // Acceso a los ImageButtons por el id
        ImageButton imgbtn_map = (ImageButton) findViewById(R.id.imgbtn_map);
        ImageButton imgbtn_place = (ImageButton) findViewById(R.id.imgbtn_place);

        imgbtn_map.setOnClickListener(this);
        imgbtn_place.setOnClickListener(this);




    }
    // Accion cuando se pulse los ImageButtons
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            // ImageButton del mapa (mostrara un mapa)
            // Lanzaremos la actividad del mapa
            case R.id.imgbtn_map:

                Intent intent_map = new Intent(this,MapaLugaresActivity.class);
                startActivity(intent_map);


                break;
            // ImageButton del lugares (mostrara un listado de lugares)
            // Lanzaremos la actividad de lugares
            case R.id.imgbtn_place:

                Intent intent_place = new Intent(PrincipalActivity.this,ListaLugaresActivity.class);
                startActivity(intent_place);


                break;


        }

    }

    // Cambiamos el idioma si se ha producido algun cambio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                //Aplicamos las preferencias del idioma
                PreferenceManager.setDefaultValues(this, R.xml.preference, false);
                SharedPreferences p = PreferenceManager
                        .getDefaultSharedPreferences(this);
                String idiomaUsuario = p.getString("pref_idiom", "en");
                //Toast.makeText(this,""+idiomaUsuario, Toast.LENGTH_SHORT).show();
                setLocale(idiomaUsuario);
                Intent refresh = new Intent(this, PrincipalActivity.class);
                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(refresh);


                break;
        }
    }
    // Funcion que pasado un idioma cambiara el uso de nuestro fichero string
    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }

    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            // Caso de pulsar el menu info
            case R.id.menu_info:
                showDialog(DIALOG_ABOUT);

                return true;
            // caso de pulsar el submenu preferencias
            case R.id.SubMnuPreferencias:

                Intent i = new Intent(this, PreferenciasActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);


                return true;
            // caso de pulsar el submenu salir
            case R.id.SubMnuSalir:
                finish();
                return true;

            default:
                return false;

        }

    }
    // Dialogos
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            // Dialogo informacion
            case DIALOG_ABOUT:
                AlertDialog dialogAbout = null;
                final AlertDialog.Builder builder;

                LayoutInflater li = LayoutInflater.from(this);
                View view = li.inflate(R.layout.acercade, null);

                builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_app)
                        .setTitle(getString(R.string.menuTitulo))
                        .setPositiveButton("Ok", null).setView(view);

                dialogAbout = builder.create();

                return dialogAbout;


            default:
                return null;
        }

    }



}
