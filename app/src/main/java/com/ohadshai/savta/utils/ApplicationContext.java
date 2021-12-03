package com.ohadshai.savta.utils;

import android.app.Application;
import android.content.Context;

public class ApplicationContext extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
