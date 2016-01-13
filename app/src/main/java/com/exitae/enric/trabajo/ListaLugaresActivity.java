package com.exitae.enric.trabajo;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.exitae.enric.trabajo.db.LugaresProvider;


public class ListaLugaresActivity extends ListActivity {

    private Cursor cursor;
    private static final int DIALOG_ABOUT = 0;
    private static final int DIALOG_ALERT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lugares_layout);
        setTitle(R.string.menuTitulo);


        // Para que el boton icono de la aplicacion se pueda usar para volver

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            // No tiene que activar el boton
        }



        // Leemos los datos de la base de datos y los añadimos al listView
        /*
		 * Paso 1: Obtenemos un cursor con todos los lugares de la base de
		 * datos
		 */


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


		 //Paso 2: mapeamos los datos del cursor para asociarlos a los campos de
		 // la vista

        String[] camposDb = new String[] { LugaresProvider.NOMBRE, LugaresProvider.DESCRIPCION,
                LugaresProvider.LATITUD, LugaresProvider.LONGITUD, LugaresProvider.FOTO };
        int[] camposView = new int[] { android.R.id.text1, android.R.id.text2 };


		  //Paso 3: creamos el Adapter


        // Con los objetos anteriores creamos el adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                cursor, camposDb, camposView);

        setListAdapter(adapter);


    }


    /**
     * Al pulsar sobre un lugar abriremos su detalle
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent_mostrar = new Intent();
        intent_mostrar.setClass(ListaLugaresActivity.this, MostrarLugarActivity.class);
        intent_mostrar.putExtra("idLugar", id);
        startActivity(intent_mostrar);
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
                        ListaLugaresActivity.this.finish();
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
