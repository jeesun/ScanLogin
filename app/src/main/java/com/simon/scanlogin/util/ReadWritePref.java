package com.simon.scanlogin.util;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.simon.scanlogin.base.BaseApplication;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by simon on 2017/4/23.
 */

public class ReadWritePref {
    private static ReadWritePref instance;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();

    @SuppressLint("CommitPrefEdits")
    private ReadWritePref() {
        pref = BaseApplication.getInstance().getSharedPreferences("data", MODE_PRIVATE);
        editor = pref.edit();
    }

    public static ReadWritePref getInstance(){
        if(null == instance){
            instance = new ReadWritePref();
        }
        return instance;
    }

    public void put(String key, String value){
        w.lock();
        try{
            editor.putString(key, value);
            editor.apply();
        }finally {
            w.unlock();
        }
    }

    public void put(String key, Long value){
        w.lock();
        try{
            editor.putLong(key, value);
            editor.apply();
        }finally {
            w.unlock();
        }
    }

    public void put(String key, Integer value){
        w.lock();
        try{
            editor.putInt(key, value);
            editor.apply();
        }finally {
            w.unlock();
        }
    }

    public String getStr(String key){
        r.lock();
        try{
            return  pref.getString(key, "");
        }finally {
            r.unlock();
        }
    }

    public int getInt(String key){
        r.lock();
        try{
            return  pref.getInt(key, -1);
        }finally {
            r.unlock();
        }
    }

    public boolean getBool(String key){
        r.lock();
        try{
            return  pref.getBoolean(key, false);
        }finally {
            r.unlock();
        }
    }

    public long getLong(String key){
        r.lock();
        try{
            return  pref.getLong(key, -1);
        }finally {
            r.unlock();
        }
    }
}
