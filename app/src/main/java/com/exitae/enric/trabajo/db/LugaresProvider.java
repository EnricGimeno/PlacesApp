package com.exitae.enric.trabajo.db;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LugaresProvider extends ContentProvider{

    public static final String PROVIDER_NAME = "es.exitae.lugar";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/lugares");

    public static final String _ID = "_id";
    public static final String NOMBRE = "nombre";
    public static final String DESCRIPCION = "descripcion";
    public static final String LATITUD = "latitud";
    public static final String LONGITUD = "longitud";
    public static final String FOTO = "foto";

    private static final int LUGAR = 1;
    private static final int LUGAR_ID = 2;
    // Creamos el uriMatcher. Esto es el listado de direcciones validas que entiende nuestro proveedor
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "lugares", LUGAR);
        uriMatcher.addURI(PROVIDER_NAME, "lugares/#", LUGAR_ID);
    }
    // Declaramos la base de datos. Objeto a traves del cual accedemos a la base de datos
    private SQLiteDatabase lugaresDB;

    /*Este método se encarga de inicializar la conexión con la base de datos. Como
    usamos nuestro Helper, si la base de datos no había sido creada, se creará en
    este momento (y si tiene que ser actualizada también lo hará entonces).*/
    @Override
    public boolean onCreate() {
        Context context = getContext();
        LugaresSQLHelper dbHelper = new LugaresSQLHelper(context);
        lugaresDB = dbHelper.getWritableDatabase();
        return (lugaresDB == null)? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(LugaresDB.Lugares.NOMBRE_TABLA);

        if (uriMatcher.match(uri) == LUGAR_ID) {
            sqlBuilder.appendWhere(LugaresDB.Lugares._ID + " = " + uri.getPathSegments().get(1));
        }

        if (sortOrder == null || sortOrder == "") {
            sortOrder = LugaresDB.Lugares.DEFAULT_SORT_ORDER;
        }

        Cursor c = sqlBuilder.query(lugaresDB, projection, selection,
                selectionArgs, null, null, sortOrder);

        // Registramos los cambios para que se enteren nuestros observers
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /*El método getType() devuelve el MimeType que se aplicará a los objetos de res-
    puesta. En nuestro caso dos, uno para los listados de registros y otro para un
    solo registro.*/
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            // Conjunto de lugares
            case LUGAR:
                return "vnd.android.cursor.dir/vnd.exitae.lugares";
            case LUGAR_ID:
                return "vnd.android.cursor.item/vnd.exitae.lugares";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereargs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case LUGAR:
                count = lugaresDB.delete(LugaresDB.Lugares.NOMBRE_TABLA, where, whereargs);
                break;
            case LUGAR_ID:
                String id = uri.getPathSegments().get(1);
                count = lugaresDB.delete(LugaresDB.Lugares.NOMBRE_TABLA,  LugaresDB.Lugares._ID + " = " + id
                                + (!TextUtils.isEmpty(where)? " AND (" + where + ')' : ""),
                        whereargs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Añaade un nuevo lugar
        long rowID = lugaresDB.insert(LugaresDB.Lugares.NOMBRE_TABLA, "", values);

        // si todo ha ido ok devolvemos su Uri
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case LUGAR:
                count = lugaresDB.update(LugaresDB.Lugares.NOMBRE_TABLA, values, selection,
                        selectionArgs);
                break;
            case LUGAR_ID:
                count = lugaresDB.update(LugaresDB.Lugares.NOMBRE_TABLA, values,  LugaresDB.Lugares._ID
                        + " = "
                        + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
