package com.jaram.jarambuild.imageUtils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageHelpers
{
    private ImageHelpers()
    {
    }

    //helperclass for image manipulation & file creation/destruction

    private Bitmap imageAutoRotation(Bitmap bitmap)
    {
        if (bitmap.getWidth() > bitmap.getHeight()) //flip in the case its a samsung.. le sigh
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.preScale(1, 1);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return bitmap;
        }
        return bitmap;
    }
}
