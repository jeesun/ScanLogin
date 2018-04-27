package com.simon.scanlogin.proxy;

import java.lang.reflect.Proxy;

import retrofit2.Retrofit;

/**
 * Created by simon on 2017/2/22.
 */

public class ApiServiceProxy {
    private Retrofit retrofit;

    private ProxyHandler proxyHandler;

    public ApiServiceProxy(Retrofit retrofit, ProxyHandler proxyHandler) {
        this.retrofit = retrofit;
        this.proxyHandler = proxyHandler;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> tClass) {
        T t = retrofit.create(tClass);
        proxyHandler.setTarget(t);
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[] { tClass }, proxyHandler);
    }
}
