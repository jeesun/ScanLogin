package com.simon.scanlogin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.simon.scanlogin.R;
import com.simon.scanlogin.interfaces.RequestServes;
import com.simon.scanlogin.config.AppConfig;
import com.simon.scanlogin.domain.AccessToken;
import com.simon.scanlogin.domain.ResultMsg;

import java.util.Date;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScanLoginActivity extends AppCompatActivity {
    private static final String TAG = ScanLoginActivity.class.getName();
    @BindView(R.id.check_login) Button btnCheckLogin;
    @BindView(R.id.cancel_login) Button btnCancelLogin;
    private String sid;
    private String username;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_login);
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        Log.i(TAG, "sid=" + sid);

        btnCheckLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences spf = getSharedPreferences("data", MODE_PRIVATE);
                username = spf.getString("username", "");
                token = spf.getString("access_token", "");

                Long date = spf.getLong("date", -1L);//单位毫秒
                Integer expiresIn = spf.getInt("expires_in", -1);//单位秒
                String refresh_token = spf.getString("refresh_token", "");
                Long currentDate = new Date().getTime();

                //检查refresh_token是否过期
                //如果还有1分钟过期，跳转到登录界面
                if((date + 5184000 - currentDate) < 60 * 1000){
                    Intent loginIntent = new Intent(ScanLoginActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }

                //检查access_token是否过期
                //如果还有1分钟access_token到期
                if((date + expiresIn * 1000 - currentDate) < 60 * 1000){
                    //使用refresh_token获取新的access_token
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.baseUrl).addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient()).build();
                    RequestServes requestServes = retrofit.create(RequestServes.class);
                    Call<ResultMsg> call = requestServes.refreshToken(refresh_token);
                    call.enqueue(new Callback<ResultMsg>() {
                        @Override
                        public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                            AccessToken accessToken = JSON.parseObject(JSON.toJSONString(response.body()), AccessToken.class);
                            SharedPreferences spf = getSharedPreferences("data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = spf.edit();
                            editor.putString("access_token", accessToken.getAccess_token());
                            editor.putLong("date", new Date().getTime());
                            editor.putInt("expires_in", accessToken.getExpires_in());
                            editor.apply();

                            loginWeb();
                        }

                        @Override
                        public void onFailure(Call<ResultMsg> call, Throwable t) {

                        }
                    });
                }else{
                    loginWeb();
                }
            }
        });

        btnCancelLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loginWeb(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.baseUrl).addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient()).build();
        RequestServes requestServes = retrofit.create(RequestServes.class);
        Call<ResultMsg> call = requestServes.loginByQrCode(username, token, sid);
        call.enqueue(new Callback<ResultMsg>() {
            @Override
            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                Log.i(TAG, response.body().toString());
                //Toast.makeText(MainActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResultMsg> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Toast.makeText(ScanLoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
