package com.simon.scanlogin.factory;

import com.simon.scanlogin.config.AppConfig;
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

    }

    public static synchronized RequestServesFactory getInstance(){
        if(null == requestServesFactory){
            requestServesFactory = new RequestServesFactory();
        }
        return requestServesFactory;
    }

    @Deprecated
    public <T> T createRequest(Class<T> tClass){
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.OAUTH_BASIC_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return new ApiServiceProxy(retrofit, new ProxyHandler()).getProxy(tClass);
    }

    public <T> T createRequest(String baseUrl, Class<T> tClass){
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return new ApiServiceProxy(retrofit, new ProxyHandler()).getProxy(tClass);
    }
}
