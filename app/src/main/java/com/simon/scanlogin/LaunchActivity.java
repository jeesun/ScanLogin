package com.simon.scanlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;

import java.util.Date;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //检查是不是第一次运行
        SharedPreferences spf = getSharedPreferences("data", MODE_PRIVATE);
        boolean firstLaunch = spf.getBoolean("firstLaunch", true);
        if (firstLaunch){
            //跳转到登录界面
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            //跳转到主界面
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
