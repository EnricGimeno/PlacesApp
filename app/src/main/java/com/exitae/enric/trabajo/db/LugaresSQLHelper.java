package com.exitae.enric.trabajo.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LugaresSQLHelper extends SQLiteOpenHelper{

    public LugaresSQLHelper(Context context) {
        super(context, LugaresDB.DB_NAME, null,LugaresDB.DB_VERSION);
    }
    // Creacion de la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        if(db.isReadOnly()) { db=getWritableDatabase(); }

        db.execSQL("CREATE TABLE " + LugaresDB.Lugares.NOMBRE_TABLA + " (" +
                        LugaresDB.Lugares._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        LugaresDB.Lugares.CAMPO_NOMBRE + " TEXT," +
                        LugaresDB.Lugares.CAMPO_DESCRIPCION + " TEXT," +
                        LugaresDB.Lugares.CAMPO_LATITUD + " DOUBLE," +
                        LugaresDB.Lugares.CAMPO_LONGITUD + " DOUBLE," +
                        LugaresDB.Lugares.CAMPO_FOTO + " TEXT" +
                        ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cuando haya cambios en la estuctura deberemos incluir el codigo
        // SQL necesario para actualizar la base de datos
        // tendremos en cuenta la version antigua y la nueva para aplicar solo
        // los necesarios

    }
}
