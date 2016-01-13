package com.exitae.enric.trabajo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.exitae.enric.trabajo.db.LugaresProvider;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;


public class MapaLugaresActivity extends FragmentActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final int DIALOG_ABOUT = 0;
    private static final int DIALOG_ALERT = 1;
    private GoogleMap map;
    private UiSettings mapSettings;

    private static final int NUM_ACTIVITY = 1;

    private Cursor cursor;
    /* Creamos una coleccion de datos donde se asociaran
     los datos de los id de los marcadores en relacion con los Id de la
     base de datos*/
    private Map<String,Integer> asociacionID = new HashMap<String,Integer>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_layout);
        setTitle(R.string.menuTitulo);

        // Comprobamos si hay internet
        if (isOnline()==true){
            // Hay conexion ha internet puede seguir

        }else{
            Toast.makeText(this, R.string.internet, Toast.LENGTH_SHORT).show();
            finish();
        }



        // Borramos el hasmap entero
        asociacionID.clear();
        // Para que el boton icono de la aplicacion se pueda usar para volver
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            // No tiene que activar el boton
        }

        // Localizamos el mapa y lo inicializamos
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        // Configure the map
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Activamos los controles de zoom
        mapSettings = map.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        // Ponemos la brujula visible.
        mapSettings.isCompassEnabled();

        // Mostramos los lugares de la base de datos
        mostrarLugaresMapa();


        // Vamos a asociar un listener a la hora de pulsar en el mapa
        map.setOnMapLongClickListener(this);

        // Vamos a asociar un listener a la hora de pulsar en la info del marker para mostrarlo en detalle
        map.setOnInfoWindowClickListener(this);


    }

    //Funcion para comprobar la conexion a internet
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }


    private void mostrarLugaresMapa() {
        // Leemos la base de datos y mostramos mediante markers los lugares
        final String[] columnas = new String[] {
                LugaresProvider._ID, // 0
                LugaresProvider.NOMBRE, // 1
                LugaresProvider.DESCRIPCION, //2
                LugaresProvider.LATITUD, // 3
                LugaresProvider.LONGITUD, //4
                LugaresProvider.FOTO, //5

        };

        Uri uri = Uri.parse("content://es.exitae.lugar/lugares/*");
        // Query "managed": la actividad se encargar de cerrar y volver a
        // cargar el cursor cuando sea necesario
        cursor = managedQuery(uri, columnas, null, null, null);

        cursor.setNotificationUri(getContentResolver(), uri);



        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            double latitud = cursor.getDouble(3);
            double longitud = cursor.getDouble(4);
            String titulo = cursor.getString(1);
            Integer id_bd = cursor.getInt(0);

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitud, longitud))
                    .title(titulo));
            String idmarker = marker.getId();
            // Asociamos en una coleccion el id del marker al id de la base de datos.
            asociacionID.put(idmarker,id_bd);


        }

    }

    // Lo que debe ocurrir cuando pulsemos la info del marker --> mostrar actividad de detalle
    @Override
    public void onInfoWindowClick(Marker marker) {
        // Obtenemos el id del marker
        String valor = marker.getId();
        // obtenemos el Id de la base de datos asociado al id del marker para poder mostrar en otro
        // layout los datos concretos de este "lugar"
        long idBD =Long.parseLong(asociacionID.get(valor).toString()) ;
        finish();
        Intent intent_mostrar = new Intent();
        intent_mostrar.setClass(MapaLugaresActivity.this, MostrarLugarActivity.class);

        intent_mostrar.putExtra("idLugar", idBD);
        startActivity(intent_mostrar);

    }

    // Lo que debe ocurrir cuando pulsemos prolongadamente en el mapa
    @Override
    public void onMapLongClick(LatLng latLng) {
        finish();
        Intent intent_editar = new Intent(MapaLugaresActivity.this, EditarLugarActivity.class);
        // Valor que nos ayudara a distingir que layot abrir en la actividad EditarLugarActivity
        intent_editar.putExtra("Num_Activity",NUM_ACTIVITY);
        double Latitud = latLng.latitude;
        double Longitud = latLng.longitude;
        intent_editar.putExtra("latitud", Latitud);
        intent_editar.putExtra("longitud", Longitud);
        startActivity(intent_editar);
        finish();

    }


    // MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_interior,menu);
        return true;

    }

    @Override
    public boolean  onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_salir:
                showDialog(DIALOG_ALERT);
                return  true;
            case R.id.menu_info:
                showDialog(DIALOG_ABOUT);

                //Toast.makeText(this,item.getTitle(),Toast.LENGTH_SHORT).show();
                return  true;

            case  android.R.id.home:
                Intent intent = new Intent(this, PrincipalActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
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

            case DIALOG_ALERT:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                dialog.setMessage(R.string.preg_salir);
                dialog.setCancelable(false);
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MapaLugaresActivity.this.finish();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();

            default:
                return null;
        }

    }


}


