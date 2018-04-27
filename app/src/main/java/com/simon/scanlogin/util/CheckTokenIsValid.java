package com.simon.scanlogin.util;

import com.simon.scanlogin.exception.UserNotLoginException;

/**
 * 检查access_token是否过期
 *
 * @author simon
 * @create 2018-04-27 12:49
 **/

public class CheckTokenIsValid {
    private static final String TAG = CheckTokenIsValid.class.getName();

    public static boolean isValid(String access_token) throws UserNotLoginException{
        Long timestamp = ReadWritePref.getInstance().getLong("timestamp");
        Integer expires_in = ReadWritePref.getInstance().getInt("expires_in");
        String refresh_token = ReadWritePref.getInstance().getStr("refresh_token");

        if("".equals(access_token) || "".equals(refresh_token) || -1 == timestamp && -1 == expires_in){
            LogUtil.e(TAG, "用户未登录");
            throw new UserNotLoginException();
        }else if(!"".equals(access_token) && !"".equals(refresh_token) && -1 != timestamp && -1 != expires_in){
            if(expires_in > (System.currentTimeMillis()-timestamp)/1000 + 60){
                LogUtil.e(TAG, "access_token过期");
                return false;
            }else{
                LogUtil.i(TAG, "access_token未过期");
                return true;
            }
        }else{
            LogUtil.i(TAG, "!!!!!!!!!!!!!!!!!!");
        }
        return false;
    }
}
