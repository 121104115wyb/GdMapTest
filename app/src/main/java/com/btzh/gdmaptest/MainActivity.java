package com.btzh.gdmaptest;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.btzh.gdmaptest.service.LocationForegoundService;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private Intent serviceIntent = null;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent();
        serviceIntent.setClass(this, LocationForegoundService.class);

        initGdMapLocation();
    }

    void initGdMapLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());

        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        StringBuffer stringBuffer = new StringBuffer();
                        //可在其中解析amapLocation获取相应内容。
                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        stringBuffer.append(aMapLocation.getLocationType() + "\n");

                        aMapLocation.getLatitude();//获取纬度
                        stringBuffer.append(aMapLocation.getLatitude() + "\n");
                        aMapLocation.getLongitude();//获取经度
                        stringBuffer.append(aMapLocation.getLongitude() + "\n");
                        aMapLocation.getAccuracy();//获取精度信息
                        aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        stringBuffer.append(aMapLocation.getAddress() + "\n");
                        aMapLocation.getCountry();//国家信息
                        aMapLocation.getProvince();//省信息
                        aMapLocation.getCity();//城市信息
                        aMapLocation.getDistrict();//城区信息
                        aMapLocation.getStreet();//街道信息
                        aMapLocation.getStreetNum();//街道门牌号信息
                        aMapLocation.getCityCode();//城市编码
                        aMapLocation.getAdCode();//地区编码
                        aMapLocation.getAoiName();//获取当前定位点的AOI信息
                        aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                        aMapLocation.getFloor();//获取当前室内定位的楼层
                        aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                        //获取定位时间
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);

                        Toast.makeText(MainActivity.this, stringBuffer.toString(), Toast.LENGTH_SHORT).show();


                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }

            }
        };

        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //签到模式（签到，出行，运动场景，默认无场景）
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
//        if (null != mLocationClient) {
//            mLocationClient.setLocationOption(mLocationOption);
//            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            mLocationClient.stopLocation();
//            mLocationClient.startLocation();
//        }

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

//        //获取一次定位结果：
//        //该方法默认为false。
//        mLocationOption.setOnceLocation(true);
//
//         //获取最近3s内精度最高的一次定位结果：
//        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//        mLocationOption.setOnceLocationLatest(true);

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);

        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //如果已经开始定位了，显示通知栏
        if (mLocationClient != null && mLocationClient.isStarted()) {
            if (null != serviceIntent) {
                startService(serviceIntent);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // mLocationClient.enableBackgroundLocation(2001, buildNotification());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果要一直显示可以不执行
        if (null != serviceIntent) {
            stopService(serviceIntent);
        }
        //腾讯高德：31.3036350951,120.6408691406
        //changeConver(31.3036350951,120.6408691406);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        //如果要一直显示可以不执行
        if (null != serviceIntent) {
            stopService(serviceIntent);
        }

    }

    public void startLocationClick(View view) {
        //再次检测权限的开启
        dynamicPermissions();
    }

    public void stopLocationClick(View view) {
        if (null != mLocationClient) {
            //启动定位
            mLocationClient.stopLocation();
        }
    }


    private void dynamicPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(granted -> {
                    if (granted) {
                        if (null != mLocationClient) {
                            //启动定位
                            mLocationClient.startLocation();
                        } else {
                            Toast.makeText(this, "参数出错！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "你需要同意权限的申请\n否则会导致定位功能不可使用", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //坐标转换(维度，经度)
    private void changeConver(double latitude, double longitude) {
        CoordinateConverter converter = new CoordinateConverter(MainActivity.this);

        boolean isAMapDataAvailable = converter.isAMapDataAvailable(latitude, longitude);
        if (isAMapDataAvailable) {

            DPoint point = new DPoint(latitude, longitude);
            // CoordType.GPS 待转换坐标类型
            //BAIDU, MAPBAR, MAPABC, SOSOMAP, ALIYUN, GOOGLE, GPS;
            //转换为百度坐标偏差最大，其次为 MAPBAR，MAPABC，ALIYUN，GOOGLE
            converter.from(CoordinateConverter.CoordType.ALIYUN);
            // sourceLatLng待转换坐标点 DPoint类型
            try {
                converter.coord(point);
                // 执行转换操作
                DPoint desLatLng = converter.convert();

                Log.d(TAG, "changeConver: "+"latitude:"+desLatLng.getLatitude()+"\n"+"longitude:"+longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        谷歌地图：31.3036216235,120.6408399011
//        百度地图：31.3093802970,120.6474029384
//        腾讯高德：31.3036350951,120.6408691406
//        图吧地图：31.3060859751,120.6466002906
//        谷歌地球：31.3057759751,120.6366502906
//        北纬N31°18′20.79″ 东经E120°38′11.94″


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

         getMenuInflater().inflate(R.menu.test,menu);

         return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.test1:
                Intent intent1 = new Intent(this,Main2Activity.class);
                startActivity(intent1);

                break;
            case R.id.test2:
                Intent intent2 = new Intent(this,Main3Activity.class);
                startActivity(intent2);
                break;
            case R.id.test3:
                Intent intent3 = new Intent(this,Main3Activity.class);
                startActivity(intent3);

                break;
            case R.id.test4:
                Intent intent4 = new Intent(this,Main4Activity.class);
                startActivity(intent4);

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
