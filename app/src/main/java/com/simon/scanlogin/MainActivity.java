package com.simon.scanlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private Button button;
    private int REQUEST_CODE_SCAN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果不传 ZxingConfig的话，两行代码就能搞定了
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
//intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Toast.makeText(MainActivity.this, "扫描结果为：" + content, Toast.LENGTH_SHORT).show();
                Log.i(TAG, content);
                LoginCode loginCode = JSON.parseObject(content, LoginCode.class);
                Log.i(TAG, loginCode.toString());
                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.0.105:8090").addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient()).build();
                RequestServes requestServes = retrofit.create(RequestServes.class);
                Call<ResultMsg> call = requestServes.loginByQrCode("jeesun", "", loginCode.getSid());
                call.enqueue(new Callback<ResultMsg>() {
                    @Override
                    public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                        Log.i(TAG, response.body().toString());
                        //Toast.makeText(MainActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResultMsg> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
