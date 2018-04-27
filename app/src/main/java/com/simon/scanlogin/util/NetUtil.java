package com.simon.scanlogin.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络连接检查
 *
 * @author simon
 * @create 2018-04-27 17:37
 **/

public class NetUtil {
    public static boolean isNetworkConnected(Context context){
        if (null != context){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (null != networkInfo){
                return networkInfo.isAvailable();
            }
        }
        return false;
    }
}
