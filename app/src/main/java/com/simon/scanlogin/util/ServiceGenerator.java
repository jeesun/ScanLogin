package com.simon.scanlogin.util;

import android.util.Base64;

import com.simon.scanlogin.config.AppConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by simon on 2017/2/22.
 */

public class ServiceGenerator {
    private OkHttpClient httpClient;
    private Retrofit.Builder builder;

    public ServiceGenerator(String baseUrl) {
        this.builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    public <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public <S> S createService(Class<S> serviceClass, String username, String password) {
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            //版本bug
            /*httpClient.interceptors().clear();
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                    .header("Accept", "applicaton/json")
                    .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });*/
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("Authorization", basic)
                                    //向Spring Boot Oauth服务器请求access token时，返回头里的Content Type是application/json;charset=UTF-8
                                    //所以这里的Accept必须填application/json;charset=UTF-8，填application/json会报406的错误
                                    .header("Accept", "application/json;charset=UTF-8")
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    }).build();
        }else{
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    //向Spring Boot Oauth服务器请求access token时，返回头里的Content Type是application/json;charset=UTF-8
                                    //所以这里的Accept必须填application/json;charset=UTF-8，填application/json会报406的错误
                                    .header("Accept", "application/json;charset=UTF-8")
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    }).build();
        }

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}