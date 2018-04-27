package com.simon.scanlogin.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.simon.scanlogin.activity.ScanLoginActivity;
import com.simon.scanlogin.config.AppConfig;
import com.simon.scanlogin.domain.AccessToken;
import com.simon.scanlogin.interfaces.OauthServes;
import com.simon.scanlogin.util.LogUtil;
import com.simon.scanlogin.util.ServiceGenerator;

import java.util.concurrent.TimeUnit;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 刷新token
 *
 * @author simon
 * @create 2018-04-26 16:59
 **/

public class RefreshToken  implements
        Func1<Observable<? extends Throwable>, Observable<?>> {
    private static final String TAG = RefreshToken.class.getName();

    private Context context;

    public RefreshToken(Context context) {
        this.context = context;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        SharedPreferences spf = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String refresh_token = spf.getString("refresh_token", "");

        OauthServes oauthServes = new ServiceGenerator(AppConfig.OAUTH_BASIC_URL).createService(OauthServes.class, "clientIdPassword", "secret");
        oauthServes
                .getToken("refresh_token", refresh_token)
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<AccessToken>() {
            @Override
            public void onCompleted() {
                LogUtil.i(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "onError");
            }

            @Override
            public void onNext(AccessToken accessToken) {
                LogUtil.i(TAG, "onNext");
                LogUtil.i(TAG, accessToken.toString());
                ScanLoginActivity.access_token = accessToken.getAccess_token();
            }
        });
        return Observable.timer(5000,
                TimeUnit.MILLISECONDS);
    }
}
