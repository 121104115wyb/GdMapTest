package com.btzh.gdmaptest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.logging.Logger;

/**
 * Created by wyb on 2018/5/7.
 */

public class SplashActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        //CheckPermission();
        dynamicPermissions();
    }

    /****************************************权限检测****************************************************/
    /**
     * 检查权限
     */
    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //权限不足就需要去申请，上下文，需要申请的权限，请求码（唯一就行）
            ActivityCompat.requestPermissions(SplashActivity.this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            //downloadApk();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * 回调的权限请求结果，是否同意都会调用
     *
     * @param requestCode  请求码
     * @param permissions  申请的权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //downloadApk();

                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
                //ToastUtil.show(getApplicationContext(), "权限不足，不能更新，下次开启请允许权限，如没有弹出，请到设置中心开启权限");
                //enterHome();
            }
        }
    }

    /******************************************权限检测**************************************************/
    //权限申请经过测试（只需要，申请 Manifest.permission.ACCESS_COARSE_LOCATION，即可定位成功）
    private void dynamicPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        lanuchApp();
                    } else {
                        lanuchApp();
                    }
                });
    }

    private void lanuchApp() {
        Handler handler = new Handler();
        //延迟1s启动界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enterLogin();
                finish();
            }
        }, 1500);
    }


    void enterLogin() {
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }


}
