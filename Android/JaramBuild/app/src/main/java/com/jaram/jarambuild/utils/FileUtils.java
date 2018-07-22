package com.jaram.jarambuild.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jaram.jarambuild.R;

public class FileUtils
{
    private static final String FOLDER_NAME = "StableMate";

    //create folder to store images OR if folder exists return folder
    private static File createFolders()
    {
        File baseDir;
        baseDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (baseDir == null)
            return Environment.getExternalStorageDirectory();
        File stableFolder = new File(baseDir, FOLDER_NAME);
        if (stableFolder.exists())
            return stableFolder;
        if (stableFolder.isFile())
            stableFolder.delete();
        if (stableFolder.mkdirs())
            return stableFolder;
        return Environment.getExternalStorageDirectory();
    }

    public static boolean deleteFile(String path)
    {
        File file;
        try
        {
            file = new File(path);
        } catch (NullPointerException e)
        {
            return false;
        }

        if (file.exists())
        {
            return file.delete();
        }
        return false;
    }


    public static String saveBitmap(String bitName, Bitmap mBitmap)
    {
        File baseFolder = createFolders();
        File f = new File(baseFolder.getAbsolutePath(), bitName);
        FileOutputStream fOut = null;
        try
        {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try
        {
            assert fOut != null;
            fOut.flush();
            fOut.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    public static long getFolderSize(File file) throws Exception
    {
        long size = 0;
        try
        {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList)
            {
                if (aFileList.isDirectory())
                {
                    size = size + getFolderSize(aFileList);
                } else
                {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return size;
    }

    public static String getFormatSize(double size)
    {
        double kiloByte = size / 1024d;
        int megaByte = (int) (kiloByte / 1024d);
        return megaByte + "MB";
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SMate_" + timeStamp + "_";
        File storageDir = FileUtils.createFolders();
        if (storageDir != null)
        {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".bmp",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            String mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
        return null;
    }
}