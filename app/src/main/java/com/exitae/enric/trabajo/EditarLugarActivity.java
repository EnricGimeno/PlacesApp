package com.exitae.enric.trabajo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import com.exitae.enric.lib_imagen.ImageFunctions;
import com.exitae.enric.trabajo.db.LugaresDB;
import com.exitae.enric.trabajo.db.LugaresProvider;


public class EditarLugarActivity extends Activity implements View.OnClickListener {

    private static final int DIALOG_ABOUT = 0;
    private static final int DIALOG_ALERT = 1;

    // Datos comunes
    private ImageView imagen;
    private Button btnImagen, btnCrear, btnEliminar, btnGuardar;
    private static int NUM_ACTIVIDAD = 0;
    private static final int REQUEST_GALERIA = 2000;
    private Cursor cursor;

    //Datos del formulario en el caso de CREAR
    private EditText nombre;
    private EditText descripcion;
    private String rutaGaleria = "";
    private double latitud;
    private double longitud;
    private boolean CAMPOS_RELLENADOS = false;

    // Datos del formulario en el caso de EDITAR
    private String textoeditarNombre;
    private String textoeditarDescripcion;
    private int width;
    private long ID_LUGAR;

    //Key imagen (orientacion)
    static final String STATE_GALERY_IMAGE = "rutaGaleria";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.menuTitulo);

        // Cambios producidos por el cambio en la orientacion del terminal.
        // concretamente refrescar la imagen
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            rutaGaleria = savedInstanceState.getString(STATE_GALERY_IMAGE);
            //Redimensionamos la imagen (Libreria imagen)
            Bitmap resizedBitmap = ImageFunctions.decodeSampledBitmapFromFile(rutaGaleria, width);
            // Asociamos la imagen al imageView (Libreria imagen)
            //imagen.setImageBitmap(resizedBitmap);


        } else {
            // Probably initialize members with default values for a new instance
        }


        // Para que el boton icono de la aplicacion se pueda usar para volver
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            // No tiene que activar el boton
        }

        Bundle bundle = getIntent().getExtras();
        NUM_ACTIVIDAD = bundle.getInt("Num_Activity");

        // Depende de la actividad en la que estemos (MAPA o LUGARES)
        // deberemos abrir un layout para crear o editar un lugar.
        // Y inicializar sus respectivos contenidos
        switch (NUM_ACTIVIDAD) {
            case 1:
                setContentView(R.layout.crear_lugar_layout);

                nombre = (EditText) findViewById(R.id.edtNombre1);
                descripcion = (EditText) findViewById(R.id.edtDescricion1);
                btnImagen = (Button) findViewById(R.id.btnSeleccionarFoto);
                btnImagen.setOnClickListener(this);
                imagen = (ImageView) findViewById(R.id.ImgViewImagen1);
                btnCrear = (Button) findViewById(R.id.btnCrear1);
                btnCrear.setOnClickListener(this);


                latitud = bundle.getDouble("latitud");
                longitud = bundle.getDouble("longitud");
                break;
            case 2:
                setContentView(R.layout.editar_lugar_layout);

                nombre = (EditText) findViewById(R.id.edtNombre2);
                descripcion = (EditText) findViewById(R.id.edtDescricion2);
                btnImagen = (Button) findViewById(R.id.btnSeleccionarFoto);
                btnImagen.setOnClickListener(this);
                imagen = (ImageView) findViewById(R.id.ImgViewImagen2);
                btnEliminar = (Button) findViewById(R.id.btnEliminar2);
                btnGuardar = (Button) findViewById(R.id.btnGuardar2);
                btnEliminar.setOnClickListener(this);
                btnGuardar.setOnClickListener(this);

                // Obtenemos los datos del formulario
                textoeditarNombre = bundle.getString("TextoNombre");
                textoeditarDescripcion = bundle.getString("TextoDescripcion");
                rutaGaleria = bundle.getString("rutaGaleria");
                width = bundle.getInt("width");
                ID_LUGAR= bundle.getLong("id_lugar");

                // Asociamos los datos en el formulario para editarlos
                nombre.setText(textoeditarNombre.toString());
                descripcion.setText(textoeditarDescripcion.toString());

                //Redimensionamos la imagen (Libreria imagen)
                Bitmap resizedBitmap = ImageFunctions.decodeSampledBitmapFromFile(rutaGaleria, width);
                // Asociamos la imagen al imageView (Libreria imagen)
                imagen.setImageBitmap(resizedBitmap);


        }

        // Leemos los datos de la base de datos

        /*
		 * Paso 1: Obtenemos un cursor con todos los lugares de la base de
		 * datos
		 */
        final String[] columnas = new String[] {
                LugaresProvider._ID, // 0
                LugaresProvider.NOMBRE, // 1
                LugaresProvider.DESCRIPCION, // 2
                LugaresProvider.LATITUD, // 3
                LugaresProvider.LONGITUD, //4
                LugaresProvider.FOTO, //5

        };

        Uri uri = Uri.parse("content://es.exitae.lugar/lugares");
        // Query "managed": la actividad se encargar de cerrar y volver a
        // cargar el cursor cuando sea necesario
        cursor = managedQuery(uri, columnas, null, null, LugaresDB.Lugares.DEFAULT_SORT_ORDER);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSeleccionarFoto:

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                // photoPickerIntent.setType("image/*");
                photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Galeria"),
                        REQUEST_GALERIA);
                break;

            case R.id.btnCrear1:

                // Comprobamos que los campos han sido rellenados al guardar
                if ((nombre.getText().toString().trim().length()==0)  || (descripcion.getText().toString().trim().length()==0)
                        || (rutaGaleria =="")){
                    Toast.makeText(this, R.string.completar, Toast.LENGTH_SHORT).show();
                }else{
                    CAMPOS_RELLENADOS = true;

                }

                if (CAMPOS_RELLENADOS == true){
                    // Añadimos los valores a la base de datos
                    Uri uri = Uri.parse("content://es.exitae.lugar/lugares");
                    ContentValues values = new ContentValues();
                    values.put("nombre", nombre.getText().toString() );
                    values.put("descripcion", descripcion.getText().toString() );
                    values.put("latitud", latitud);
                    values.put("longitud", longitud);
                    values.put("foto", rutaGaleria);
                    getContentResolver().insert(uri, values);

                    cursor.requery();

                    Toast.makeText(this, R.string.lugar_creado, Toast.LENGTH_SHORT).show();

                    finish();
                    Intent intent = new Intent(this, PrincipalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


                }

                break;

            case R.id.btnGuardar2:

                // Comprobamos que los campos han sido rellenados al guardar
                if ((nombre.getText().toString().trim().length()==0)  || (descripcion.getText().toString().trim().length()==0)
                        || (rutaGaleria =="")){
                    Toast.makeText(this, R.string.completar, Toast.LENGTH_SHORT).show();
                }else{
                    CAMPOS_RELLENADOS = true;

                }

                if (CAMPOS_RELLENADOS == true) {
                    Uri uri = Uri.parse("content://es.exitae.lugar/lugares");
                    uri = ContentUris.withAppendedId(uri, ID_LUGAR);
                    ContentValues values = new ContentValues();
                    values.put("nombre", nombre.getText().toString() );
                    values.put("descripcion", descripcion.getText().toString());
                    values.put("foto", rutaGaleria);
                    getContentResolver().update(uri, values, null, null);

                    Toast.makeText(this, R.string.lugar_modificado, Toast.LENGTH_SHORT).show();

                    finish();
                    Intent intent_lista = new Intent(this, ListaLugaresActivity.class);
                    intent_lista.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent_lista);


                }



                break;


            case R.id.btnEliminar2:
                Uri uri = Uri.parse("content://es.exitae.lugar/lugares");
                uri = ContentUris.withAppendedId(uri, ID_LUGAR);
                getContentResolver().delete(uri, null, null);

                Toast.makeText(this, R.string.lugar_eliminado, Toast.LENGTH_SHORT).show();
                finish();
                Intent intent_lista = new Intent(this, ListaLugaresActivity.class);
                intent_lista.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_lista);

                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Solo ejecutaremos si el cambio a sido correcto. Es decir que no se ha cancelado
        if (resultCode == RESULT_OK) {
            //Queremos obtener de la pantalla
            Display display = getWindowManager().getDefaultDisplay();
            int width = 400;

            // Obtencion del ancho para versiones antiguas
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
                // Ancho de pantalla
                width = display.getWidth();

                SharedPreferences sp = getSharedPreferences("width", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor_width = sp.edit();
                editor_width.putInt("width", width);
                editor_width.commit();

            // Obtencion del ancho. Nuevas versiones
            } else {
                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                width = size.x;

                SharedPreferences sp = getSharedPreferences("width", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor_width = sp.edit();
                editor_width.putInt("width", width);
                editor_width.commit();

            } if (requestCode == REQUEST_GALERIA) {

                Uri selectedImage = data.getData();
                // Obtemos la ruta de la imagen
                rutaGaleria = getRealPathFromURI(selectedImage);

                //Redimensionamos la imagen (Libreria imagen)
                Bitmap resizedBitmap = ImageFunctions.decodeSampledBitmapFromFile(rutaGaleria, width);
                // Asociamos la imagen al imageView (Libreria imagen)
                imagen.setImageBitmap(resizedBitmap);
            }

        }
    }

    //Obtenemos la ruta de la imagen de la galeria

    public String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }


    // Mantener la imagen en un cambio de orientacion de la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(STATE_GALERY_IMAGE,rutaGaleria);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rutaGaleria = savedInstanceState.getString(STATE_GALERY_IMAGE);
        //Redimensionamos la imagen (Libreria imagen)
        Bitmap resizedBitmap = ImageFunctions.decodeSampledBitmapFromFile(rutaGaleria, width);
        // Asociamos la imagen al imageView (Libreria imagen)
        imagen.setImageBitmap(resizedBitmap);
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
                        EditarLugarActivity.this.finish();
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
