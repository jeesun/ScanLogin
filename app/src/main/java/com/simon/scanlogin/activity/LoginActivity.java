package com.simon.scanlogin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.simon.scanlogin.R;
import com.simon.scanlogin.interfaces.RequestServes;
import com.simon.scanlogin.config.AppConfig;
import com.simon.scanlogin.domain.LoginInfo;
import com.simon.scanlogin.domain.ResultMsg;
import com.simon.scanlogin.util.LogUtil;

import java.util.Date;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private EditText etUsername, etPassword;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.baseUrl).addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient()).build();
                RequestServes requestServes = retrofit.create(RequestServes.class);
                Call<ResultMsg> call = requestServes.login(username, password);
                call.enqueue(new Callback<ResultMsg>() {
                    @Override
                    public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
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
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ResultMsg> call, Throwable t) {

                    }
                });
            }
        });
    }
}
