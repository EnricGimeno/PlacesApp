package com.exitae.enric.trabajo;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exitae.enric.lib_imagen.ImageFunctions;
import com.exitae.enric.trabajo.db.LugaresDB;
import com.exitae.enric.trabajo.db.LugaresProvider;



public class MostrarLugarActivity extends Activity implements View.OnClickListener {

    private static final int DIALOG_ABOUT = 0;
    private static final int DIALOG_ALERT = 1;

    private TextView nombre, descripcion;
    private ImageView imagen;
    private Button btnEditar;

    // Componenetes del formulario
    private String textoNombre;
    private String textoDescripcion;
    private String rutaGaleria;

    private  int width;
    private long idLugar;


    // Integer que nos ayuda a discernir que layout hay que abrir en la actividad de EditarLugar
    private static final int NUM_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_lugar_layout);

        setTitle(R.string.menuTitulo);

        // Para que el boton icono de la aplicacion se pueda usar para volver
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            // No tiene que activar el boton
        }

        nombre = (TextView) findViewById(R.id.txtViewNombre);
        descripcion = (TextView) findViewById(R.id.txtViewDescricion);
        imagen = (ImageView) findViewById(R.id.ImgViewImagen);
        btnEditar = (Button) findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            // Obtenemos el id del lugar que hay que mostrar
            Bundle extras = getIntent().getExtras();
            idLugar = extras.getLong("idLugar");

            //Paso 1: Obtenemos un cursor con todos los lugares de la base de
            // datos
            final String[] columnas = new String[]{
                    LugaresProvider._ID, // 0
                    LugaresProvider.NOMBRE, // 1
                    LugaresProvider.DESCRIPCION, //2
                    LugaresProvider.LATITUD, // 3
                    LugaresProvider.LONGITUD, //4
                    LugaresProvider.FOTO, //5

            };

            Uri uri = Uri.parse("content://es.exitae.lugar/lugares");
            uri = ContentUris.withAppendedId(uri, idLugar);

            // Query "managed": la actividad se encargar de cerrar y volver a
            // cargar el cursor cuando sea necesario
            Cursor cursor = managedQuery(uri, columnas, null, null,
                    LugaresDB.Lugares.DEFAULT_SORT_ORDER);

            cursor.setNotificationUri(getContentResolver(), uri);

            // Para que la actividad se encarge de manejar el cursor
            // segun sus ciclos de vida
            startManagingCursor(cursor);

            // mostramos los datos del cursor en la vista

        if (cursor.moveToFirst()) {
            textoNombre = cursor.getString(1);
            textoDescripcion = cursor.getString(2);

            nombre.setText(textoNombre);
            descripcion.setText(textoDescripcion);
            // Obtenemos las dimensiones de la pantalla que teniamos guardado en un SharedPreference
            SharedPreferences sp = getSharedPreferences("width", Activity.MODE_PRIVATE);
            width = sp.getInt("width", 400);

            rutaGaleria = new String(cursor.getString(5).getBytes(), "utf-8");
            //Redimensionamos la imagen con la funcion que esta en la libreria
            Bitmap resizedBitmap = ImageFunctions.decodeSampledBitmapFromFile(rutaGaleria, width);
            // Asociamos la imagen al imageView
            imagen.setImageBitmap(resizedBitmap);


                }
            } catch (Exception e) {
                e.printStackTrace();
             }

        }

    // Cuando pulsemos el boton
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEditar:

                Intent intent_editar = new Intent(MostrarLugarActivity.this, EditarLugarActivity.class);
                intent_editar.putExtra("Num_Activity",NUM_ACTIVITY);
                intent_editar.putExtra("TextoNombre", textoNombre);
                intent_editar.putExtra("TextoDescripcion", textoDescripcion);
                intent_editar.putExtra("rutaGaleria", rutaGaleria);
                intent_editar.putExtra("width", width);
                intent_editar.putExtra("id_lugar", idLugar);

                startActivity(intent_editar);
                finish();

                break;
        }

    }

    // MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_interior, menu);
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
                        MostrarLugarActivity.this.finish();
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


