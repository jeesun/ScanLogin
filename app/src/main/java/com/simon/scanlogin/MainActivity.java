package com.simon.scanlogin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private Button button;
    private int REQUEST_CODE_SCAN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AndPermission.with(this);

            AndPermission.with(this)
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA)
                    .rationale(new DefaultRationale())
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            Log.i(TAG, "已获得权限");
                            Toast.makeText(MainActivity.this, "已获得权限", Toast.LENGTH_SHORT).show();
                        }
                    }).onDenied(new Action() {
                @Override
                public void onAction(List<String> permissions) {
                    Log.i(TAG, "没有权限");
                    if(AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)){
                        new PermissionSetting(MainActivity.this).showSetting(permissions);
                    }
                }
            })
                    .start();
        }

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
                Intent intent = new Intent(MainActivity.this, ScanLoginActivity.class);
                intent.putExtra("sid", loginCode.getSid());
                startActivity(intent);
            }
        }
    }
}
