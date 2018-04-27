package com.simon.scanlogin.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.simon.scanlogin.R;
import com.simon.scanlogin.domain.LoginCode;
import com.simon.scanlogin.domain.ResultMsg;
import com.simon.scanlogin.domain.UserInfo;
import com.simon.scanlogin.exception.NoNetworkException;
import com.simon.scanlogin.exception.UserNotLoginException;
import com.simon.scanlogin.factory.RequestServesFactory;
import com.simon.scanlogin.interfaces.RequestWithToken;
import com.simon.scanlogin.permission.DefaultRationale;
import com.simon.scanlogin.permission.PermissionSetting;
import com.simon.scanlogin.util.LogUtil;
import com.simon.scanlogin.util.ReadWritePref;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private int REQUEST_CODE_SCAN = 111;

    @BindView(R.id.logout) Button btnLogout;

    @BindView(R.id.info)
    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AndPermission.with(this);

            AndPermission.with(this)
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA)
                    .rationale(new DefaultRationale())
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            Log.i(TAG, "已获得权限");
                            //Toast.makeText(MainActivity.this, "已获得权限", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.richScan:
                //如果不传 ZxingConfig的话，两行代码就能搞定了
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
//intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.logout) void logout(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.get_user_info) void getUserInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestWithToken requestServes = RequestServesFactory.getInstance().createRequest(RequestWithToken.class);
                try {
                    Call<ResultMsg> call = requestServes.getUser(ReadWritePref.getInstance().getStr("access_token"));
                    call.enqueue(new Callback<ResultMsg>() {
                        @Override
                        public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                            if (response.isSuccessful()){
                                Log.i(TAG, response.body().toString());
                                UserInfo userInfo = JSON.parseObject(JSON.toJSONString(response.body().getData()), UserInfo.class);
                                if(null != userInfo){
                                    tvInfo.setText(userInfo.toString());
                                    ReadWritePref.getInstance().put("username", userInfo.getUsername());
                                }
                            }else{
                                Log.i(TAG, response.errorBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultMsg> call, Throwable t) {
                            Log.i(TAG, t.toString());
                        }
                    });
                } catch (UserNotLoginException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, e.getMessage());
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } catch (NoNetworkException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, e.getMessage());
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
    }
}
