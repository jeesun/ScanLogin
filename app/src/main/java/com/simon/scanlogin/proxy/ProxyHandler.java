package com.simon.scanlogin.proxy;

import android.util.Log;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.simon.scanlogin.base.BaseApplication;
import com.simon.scanlogin.config.AppConfig;
import com.simon.scanlogin.domain.AccessToken;
import com.simon.scanlogin.domain.InvalidToken;
import com.simon.scanlogin.exception.NoNetworkException;
import com.simon.scanlogin.interfaces.OauthServesCall;
import com.simon.scanlogin.util.CheckTokenIsValid;
import com.simon.scanlogin.util.LogUtil;
import com.simon.scanlogin.util.NetUtil;
import com.simon.scanlogin.util.ReadWritePref;
import com.simon.scanlogin.util.ServiceGenerator;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by simon on 2017/2/22.
 */

/**
 * 被代理的对象是RequestServes
 * Proxy.getProxyInstance3个参数的第一个参数是被代理的类
 */
public class ProxyHandler implements InvocationHandler {
    private static final String TAG = ProgressBar.class.getName();

    private Object target;

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        Object result = method.invoke(target, args);
        Log.e(TAG, "before");
        //1、首先从SharedPreferences读取access_token和refresh_token。
        //2、发现为空，则跳转到登录页面。
        //3、有值（只要有值，两个值必然都非空），检查access_token。
        //4、如果access_token有效，啥都不做
        //5、如果access_token失效，
        // 如果refresh_token有效，根据refresh_token获取新的access_token并写入SharedPreferences
        // 如果refresh_token失效，引导到登录页面重新登陆。
        //引导到登录页面，需要将得到的access_token和refresh_token写入SharedPreferences。
        //new CheckTokenTask().execute();
        if (!NetUtil.isNetworkConnected(BaseApplication.getInstance())){
            throw new NoNetworkException("已断开网络连接");
        }
        String access_token = args[0].toString();
        LogUtil.e(TAG, "args[0]=" + args[0].toString());
        if (!CheckTokenIsValid.isValid(access_token)){
            LogUtil.e(TAG, "invalid access_token");
            String refresh_token = ReadWritePref.getInstance().getStr("refresh_token");

            OauthServesCall requestServes = new ServiceGenerator(AppConfig.OAUTH_BASIC_URL).createService(OauthServesCall.class, "clientIdPassword", "secret");
            Call<AccessToken> call = requestServes
                    .getToken("refresh_token", refresh_token);
            try {
                Response<AccessToken> response = call.execute();
                if(response.isSuccessful()){
                    AccessToken accessToken = response.body();
                    ReadWritePref.getInstance().put("access_token", accessToken.getAccess_token());
                    ReadWritePref.getInstance().put("timestamp", System.currentTimeMillis());
                    ReadWritePref.getInstance().put("expires_in", accessToken.getExpires_in());
                    LogUtil.e(TAG, "after=" + accessToken.getAccess_token());
                    args[0] = accessToken.getAccess_token();
                    result = method.invoke(target, args);
                }else if(null!=response.errorBody()){
                    InvalidToken invalidToken = JSON.parseObject(response.errorBody().string(), InvalidToken.class);
                    LogUtil.e(TAG, invalidToken.getError_description());
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.i(TAG, "IOException:"+e.getMessage());
            }

        }
        Log.e(TAG, "after");
        return result;
    }
}