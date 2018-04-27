package com.simon.scanlogin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.simon.scanlogin.R;
import com.simon.scanlogin.domain.AccessToken;
import com.simon.scanlogin.domain.InvalidToken;
import com.simon.scanlogin.impl.RetryWithDelay;
import com.simon.scanlogin.interfaces.OauthServes;
import com.simon.scanlogin.interfaces.RequestServes;
import com.simon.scanlogin.config.AppConfig;
import com.simon.scanlogin.domain.LoginInfo;
import com.simon.scanlogin.domain.ResultMsg;
import com.simon.scanlogin.util.LogUtil;
import com.simon.scanlogin.util.ServiceGenerator;

import java.io.IOException;
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
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    @BindView(R.id.username) EditText etUsername;
    @BindView(R.id.password) EditText etPassword;
    @BindView(R.id.login) Button btnLogin;
    @BindView(R.id.progressbar_wrapper) RelativeLayout rlProgressbarWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login) void login(){
        rlProgressbarWrapper.setVisibility(View.VISIBLE);
        //模仿ProgressDialog出现时，用户无法与界面继续交互的效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        /*Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.baseUrl).addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient()).build();
        RequestServes requestServes = retrofit.create(RequestServes.class);
        Call<ResultMsg> call = requestServes.login(username, password);
        call.enqueue(new Callback<ResultMsg>() {
            @Override
            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                LogUtil.i(TAG, "code=" + response.code());
                if (null != response.errorBody()){
                    LogUtil.i(TAG, "" + response.errorBody().toString());
                }
                LogUtil.i(TAG, "response=" + response.toString());
                LogUtil.i(TAG, "message=" + response.message());

                LoginInfo loginInfo = JSON.parseObject(JSON.toJSONString(response.body().getData()), LoginInfo.class);
                LogUtil.i(TAG, loginInfo.toString());

                SharedPreferences spf = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = spf.edit();
                editor.putBoolean("firstLaunch", false);
                editor.putString("access_token", loginInfo.getToken().getAccess_token());
                editor.putString("refresh_token", loginInfo.getToken().getRefresh_token());
                editor.putLong("date", new Date().getTime());
                editor.putInt("expires_in", loginInfo.getToken().getExpires_in());
                editor.putString("username", loginInfo.getUserInfo().getUsername());
                editor.apply();

                rlProgressbarWrapper.setVisibility(View.GONE);
                //恢复用户与界面的交互
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onFailure(Call<ResultMsg> call, Throwable t) {
                LogUtil.e(TAG, "onFailure");
                LogUtil.e(TAG, t.getMessage());
                LogUtil.e(TAG, t.getLocalizedMessage());
            }
        });*/
        OauthServes oauthServes = new ServiceGenerator(AppConfig.OAUTH_BASIC_URL).createService(OauthServes.class, "clientIdPassword", "secret");
        oauthServes.getToken("password", username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribe(new Subscriber<AccessToken>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i(TAG, "onCompleted");

                        rlProgressbarWrapper.setVisibility(View.GONE);
                        //恢复用户与界面的交互
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "用户名或者密码错误，或者账号被封");

                        rlProgressbarWrapper.setVisibility(View.GONE);
                        //恢复用户与界面的交互
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        LogUtil.i(TAG, "onNext");
                        rlProgressbarWrapper.setVisibility(View.GONE);
                        //恢复用户与界面的交互
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        SharedPreferences spf = getSharedPreferences("data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = spf.edit();
                        editor.putBoolean("firstLaunch", false);
                        editor.putString("access_token", accessToken.getAccess_token());
                        editor.putString("refresh_token", accessToken.getRefresh_token());
                        editor.putLong("token_request_time", new Date().getTime());
                        editor.putInt("expires_in", accessToken.getExpires_in());
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
    }
}
