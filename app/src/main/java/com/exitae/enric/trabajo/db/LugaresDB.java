package com.exitae.enric.trabajo.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class LugaresDB {

    /*
	 * Espacio de nombres (se debe usar el mismo para definir el content provider
	 * en el Manifest
	 */
    public static final String AUTHORITY = "es.exitae.lugar";

    /*
	 * Nombre de la base de datos
	 */
    public static final String DB_NAME = "lugares.db";

    /*
     * Version de la base de datos
     */
    public static final int DB_VERSION = 1;


    /**
     * Esta clase no debe ser instanciada
     */
    private LugaresDB() {
    }

    /* Definicion de la tabla lugares */
    public static final class Lugares implements BaseColumns {

        /**
         * Esta clase no debe ser instanciada
         */
        private Lugares() {}

        /**
         *  content:// estilo URL para esta tabla
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/lugares");


        /**
         * orden por defecto
         */
        public static final String DEFAULT_SORT_ORDER = "_ID DESC";

        /**
         * Abstraccion de los nombres de campos y tabla a constantes
         * para facilitar cambios en la estructura interna de la BD
         */
        public static final String NOMBRE_TABLA = "lugares";

        //public static final String _ID = "_id";
        public static final String CAMPO_NOMBRE = "nombre";
        public static final String CAMPO_DESCRIPCION = "descripcion";
        public static final String CAMPO_LATITUD = "latitud";
        public static final String CAMPO_LONGITUD = "longitud";
        public static final String CAMPO_FOTO = "foto";


    }
}
