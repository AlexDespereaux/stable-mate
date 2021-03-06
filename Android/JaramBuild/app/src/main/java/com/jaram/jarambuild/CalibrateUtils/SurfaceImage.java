package com.jaram.jarambuild.CalibrateUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceImage extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap bitmap;
    private Paint paint;
    private String TAG = "SurfaceImage";
    String imagePath = "";

    public SurfaceImage(Context context, String imagePath) {
        super(context);
        getHolder().addCallback(this);
        bitmap = BitmapFactory.decodeFile(imagePath);
        Log.d(TAG, "in surfaceImage constructor");
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Get screen size
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int maxHeight = displayMetrics.heightPixels;
        int maxWidth = displayMetrics.widthPixels;

        // Determine the constrained dimension, which determines both dimensions.
        int width;
        int height;
        if (bitmap.getWidth() > bitmap.getHeight()) //flip in the case its a samsung.. le sigh
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.preScale(1, 1);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        float widthRatio = (float)bitmap.getWidth() / maxWidth;
        float heightRatio = (float)bitmap.getHeight() / maxHeight;
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth;
            height = (int)(((float)width / bitmap.getWidth()) * bitmap.getHeight());
        }
        // Height constrained.
        else {
            height = maxHeight;
            width = (int)(((float)height / bitmap.getHeight()) * bitmap.getWidth());
        }

        float ratioX = (float)width / bitmap.getWidth();
        float ratioY = (float)height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        //canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @SuppressLint("WrongCall")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            synchronized (holder) {
                onDraw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}