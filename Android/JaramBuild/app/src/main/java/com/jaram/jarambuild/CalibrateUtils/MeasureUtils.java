package com.jaram.jarambuild.CalibrateUtils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;


public class MeasureUtils {
    private MeasureUtils(){}


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    //Imperial units conversion factor - https://en.wikipedia.org/wiki/Imperial_units
    public static double inchesToMeters(double measurement) {
        return measurement * 0.0254;
    }

    public static double yardsToMeters(double measurement) {
        return measurement * 0.9144;
    }

    public static double millimetersToMeters(double measurement) {
        return measurement * 0.001;
    }

    public static double centimetersToMeters(double measurement) {
        return measurement * 0.01;
    }

    public static double metersToYards(double measurement) {
        return measurement * 1.0936133;
    }

    public static double metersToFeet(double measurement) {
        return measurement * 3.2808399;
    }

    public static double metersToInch(double measurement) {
        return measurement * 39.3700787;
    }

    public static double metersToMillimeters(double measurement) {
        return measurement * 1000;
    }

    public static double metersToCentimeters(double measurement) {
        return measurement * 100;
    }
}
