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
    private static RequestServes requestServes;
    private static RequestWithToken requestWithToken;

    private RequestServesFactory(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.OAUTH_BASIC_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
//        requestServes = retrofit.create(RequestServes.class);
        requestServes = new ApiServiceProxy(retrofit, new ProxyHandler()).getProxy(RequestServes.class);
        requestWithToken = new ApiServiceProxy(retrofit, new ProxyHandler()).getProxy(RequestWithToken.class);
        //requestWithToken = retrofit.create(RequestWithToken.class);

    }

    public static RequestServesFactory getInstance(){
        if(null == requestServesFactory){
            requestServesFactory = new RequestServesFactory();
        }
        return requestServesFactory;
    }

    public RequestServes createRequestServes(){
        return requestServes;
    }

    public RequestServes createNoneProxyRequestServes(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(RequestServes.class);
    }

    public RequestWithToken createRequestWithToken(){
        return requestWithToken;
    }
}
