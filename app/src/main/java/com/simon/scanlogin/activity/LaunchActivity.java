package com.simon.scanlogin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.simon.scanlogin.R;

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
            finish();
        }else{
            //跳转到主界面
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
