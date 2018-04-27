package com.simon.scanlogin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.simon.scanlogin.R;
import com.simon.scanlogin.impl.RefreshToken;
import com.simon.scanlogin.interfaces.OauthServes;
import com.simon.scanlogin.interfaces.RequestServes;
import com.simon.scanlogin.config.AppConfig;
import com.simon.scanlogin.domain.AccessToken;
import com.simon.scanlogin.domain.ResultMsg;
import com.simon.scanlogin.util.LogUtil;
import com.simon.scanlogin.util.ServiceGenerator;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ScanLoginActivity extends AppCompatActivity {
    private static final String TAG = ScanLoginActivity.class.getName();
    @BindView(R.id.check_login) Button btnCheckLogin;
    @BindView(R.id.cancel_login) Button btnCancelLogin;
    @BindView(R.id.login_web_wrapper)
    RelativeLayout rlLoginWebWrapper;
    private String sid;
    public static String access_token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_login);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        Log.i(TAG, "sid=" + sid);
    }

    @OnClick(R.id.check_login) void checkLogin(){

        SharedPreferences spf = getSharedPreferences("data", Context.MODE_PRIVATE);
        access_token = spf.getString("access_token", "");
        String username = "user2711";
        RequestServes requestServes = new ServiceGenerator(AppConfig.baseUrl).createService(RequestServes.class);
        requestServes
                .loginByQrCode(username, access_token, sid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RefreshToken(ScanLoginActivity.this))
                .subscribe(new Subscriber<ResultMsg>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "onError");
                    }

                    @Override
                    public void onNext(ResultMsg resultMsg) {
                        LogUtil.i(TAG, "onNext");
                        rlLoginWebWrapper.setVisibility(View.VISIBLE);
                    }
                });
    }

    @OnClick(R.id.cancel_login) void cancelLogin(){
        finish();
    }
}
