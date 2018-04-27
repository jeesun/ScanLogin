package com.simon.scanlogin.base;

import android.app.Application;

/**
 * 全局Application
 *
 * @author simon
 * @create 2018-04-27 11:44
 **/

public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static BaseApplication getInstance(){
        return instance;
    }
}
