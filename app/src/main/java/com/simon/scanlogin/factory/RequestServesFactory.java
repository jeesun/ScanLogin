package com.simon.scanlogin.factory;

import com.simon.scanlogin.config.AppConfig;
import com.simon.scanlogin.interfaces.RequestServes;
import com.simon.scanlogin.interfaces.RequestWithToken;
import com.simon.scanlogin.proxy.ApiServiceProxy;
import com.simon.scanlogin.proxy.ProxyHandler;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by simon on 2017/4/15.
 */

public class RequestServesFactory {
    private static RequestServesFactory requestServesFactory;
    private Retrofit retrofit;

    private RequestServesFactory(){
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.OAUTH_BASIC_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static RequestServesFactory getInstance(){
        if(null == requestServesFactory){
            requestServesFactory = new RequestServesFactory();
        }
        return requestServesFactory;
    }

    public <T> T createRequest(Class<T> tClass){
        return new ApiServiceProxy(retrofit, new ProxyHandler()).getProxy(tClass);
    }
}
