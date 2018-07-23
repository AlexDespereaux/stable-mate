package com.jaram.jarambuild;

import android.app.Application;
import android.content.Context;

////Original Code Source by Burhanuddin Rashid on 1/17/2018 as part of the https://github.com/burhanrashid52/PhotoEditor
////Used under the http://www.apache.org/licenses/LICENSE-2.0
////Edited May 2018 team Jaram >> JARAM12358@gmail.com

public class PhotoApp extends Application
{
    private static PhotoApp sPhotoApp;
    private static final String TAG = PhotoApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        sPhotoApp = this;
    }

    public static PhotoApp getPhotoApp() {
        return sPhotoApp;
    }

    public Context getContext() {
        return sPhotoApp.getContext();
    }
}
