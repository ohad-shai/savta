package com.ohadshai.savta.utils;

import android.app.Application;
import android.content.Context;

public class ApplicationContext extends Application {
    private static Context _context;

    @Override
    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
    }

    public static Context getContext() {
        return _context;
    }

}
