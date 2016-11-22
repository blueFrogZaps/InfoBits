package com.bitspilani.library.infobits;

import android.app.Application;
import android.content.Context;

public class Library extends Application {

    private static Library sInstance;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Library getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}