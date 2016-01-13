package com.exitae.enric.lib_imagen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ImageFunctions{


    // Tutorial redimensionamiento:
    // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    // Solo necesitamos el ancho
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        // Leeemos el ancho de la imagen
        final int width = options.outWidth;
        // Factor de escala por defecto 1
        int inSampleSize = 1;
        // Solo entramos si el ancho es mayor que el requerido
        if (width > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        // deVolvemos +1 para usar un escalado mayor
        return inSampleSize + 1;
    }
    // devolvera la imagen redimensionada sin tener que cargar la imagen completa
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        // Con esto no cargamos la imagen
        options.inJustDecodeBounds = true;
        // recurso nuestro
        BitmapFactory.decodeFile(path, options);
        //
        options.inSampleSize = calculateInSampleSize(options, reqWidth);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}
