package com.program.customizeview;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class App extends Application {

    private static Handler sHandler = null;
    private static Context sContext = null;
    @Override
    public void onCreate() {
        super.onCreate();
        sHandler = new Handler();
        sContext = getApplicationContext();
    }

    public static Context getContext(){
        return sContext;
    }

    public static Handler getHandler(){
        return sHandler;
    }
}
