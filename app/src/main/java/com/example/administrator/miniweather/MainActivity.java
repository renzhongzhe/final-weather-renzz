package com.example.administrator.miniweather;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.administrator.miniweather.NetUtil;
import com.example.administrator.miniweather.bean.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
import com.baidu.location.Poi;
/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class MainActivity extends Activity implements View.OnClickListener , ViewPager.OnPageChangeListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect,mLocationBtn;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    private TextView weekday1,weekday2,weekday3,weekday4,temperature1,temperature2,temperature3,temperature4,weather1,weather2,weather3,weather4,
              fengli1,fengli2,fengli3,fengli4;


    private  View viewforname1,viewforname2;
    private IntentFilter intentFilter;
    private MyBroadcastReceiver myBroadcastReceiver;

    private String citycodeformain = "101010100";

    private ViewPageAdapter vpAdapter2;
    private ViewPager vp2;
    private List<View> views2;

    private ImageView[] dots2;
    private int[] ids= {R.id.ivm1,R.id.ivm2};

    private futureweather[] futureweathers;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    public BDLocation currentlocation;







    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    for(int i=0;i<4;i++)
                        futureweathers[i].Show();
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        mLocationBtn = (ImageView)findViewById(R.id.title_location);
        mLocationBtn.setOnClickListener(this);

        initView();
        initDots();

        Intent ServiceIntent = new Intent(this,MyService.class);
        startService(ServiceIntent);
        intentFilter = new IntentFilter();
        intentFilter.addAction("updatedata");
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver,intentFilter);


    }
    void initDots(){
        dots2 = new ImageView[views2.size()];
        for(int i=0;i<views2.size();i++)
        {
            dots2[i]=(ImageView) findViewById(ids[i]);
        }
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
       option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }



    @Override
    protected void onDestroy (){
        super.onDestroy();

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {
        for(int a=0;a<ids.length;a++)
        {
            if(a == i){
                dots2[a].setImageResource(R.drawable.page_indicator_focused);

            }else {
                dots2[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    void initView() {

        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");


        LayoutInflater inflater = LayoutInflater.from(this);
        views2 = new ArrayList<View>();
        viewforname1 = inflater.inflate(R.layout.weather1,null);
        viewforname2 = inflater.inflate(R.layout.weather2,null);
        views2.add(viewforname1);
        views2.add(viewforname2);
        vpAdapter2 = new ViewPageAdapter(views2,this);
        vp2 = (ViewPager)findViewById(R.id.viewpagermain);
        vp2.setAdapter(vpAdapter2);
        vp2.setOnPageChangeListener(this);

        weekday1 = (TextView)viewforname1.findViewById(R.id.day1);
        weekday2 = (TextView)viewforname1.findViewById(R.id.day2);
        weekday3 = (TextView)viewforname1.findViewById(R.id.day3);
        weekday4 = (TextView)viewforname2.findViewById(R.id.day4);

        Log.d("1",(weekday1==null)+"12");

        temperature1 = (TextView)viewforname1.findViewById(R.id.temperature1);
        temperature2 = (TextView)viewforname1.findViewById(R.id.temperature2);
        temperature3 = (TextView)viewforname1.findViewById(R.id.temperature3);
        temperature4 = (TextView)viewforname2.findViewById(R.id.temperature4);

        weather1 = (TextView)viewforname1.findViewById(R.id.weather1_specific);
        weather2 = (TextView)viewforname1.findViewById(R.id.weather2_specific);
        weather3 = (TextView)viewforname1.findViewById(R.id.weather3_specific);
        weather4 = (TextView)viewforname2.findViewById(R.id.weather4_specific);

        fengli1 = (TextView)viewforname1.findViewById(R.id.fengli1);
        fengli2 = (TextView)viewforname1.findViewById(R.id.fengli2);
        fengli3 = (TextView)viewforname1.findViewById(R.id.fengli3);
        fengli4 = (TextView)viewforname2.findViewById(R.id.fengli4);

       weekday1.setText("N/A");
        weekday2.setText("N/A");
        weekday3.setText("N/A");
        weekday4.setText("N/A");

        temperature1.setText("N/A");
        temperature2.setText("N/A");
        temperature3.setText("N/A");
        temperature4.setText("N/A");

        weather1.setText("N/A");
        weather2.setText("N/A");
        weather3.setText("N/A");
        weather4.setText("N/A");

        fengli1.setText("N/A");
        fengli2.setText("N/A");
        fengli3.setText("N/A");
        fengli4.setText("N/A");

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.title_city_manager){
            Intent i = new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }
        if (view.getId() == R.id.title_update_btn) {


            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", citycodeformain);
            Log.d("myWeather", cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);

            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
        if(view.getId() == R.id.title_location){

            Toast.makeText(MainActivity.this,"1",Toast.LENGTH_LONG).show();

            Log.d("mylocation",""+mLocationClient.isStarted());
//            mLocationClient.requestLocation();
            currentlocation = mLocationClient.getLastKnownLocation();
            String area = currentlocation.getCity();
            Toast.makeText(MainActivity.this,"当前城市："+area,Toast.LENGTH_LONG).show();
            citycodeformain = LocalCity.getCityIdByName(area);
            queryWeatherCode(citycodeformain);



        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data.getStringExtra("cityCode");
            if(newCityCode != null)
            citycodeformain = new String(newCityCode);
            Log.d("myWeather", "选择的城市代码为"+newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode) {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            mUpdateBtn.startAnimation(operatingAnim);
        }
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        Thread.sleep(2000);

                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        weekday1.setText(futureweathers[0].getWeekday());
        weekday2.setText(futureweathers[1].getWeekday());
        weekday3.setText(futureweathers[2].getWeekday());
        weekday4.setText(futureweathers[3].getWeekday());



        temperature1.setText(futureweathers[0].getHigh()+"~"+futureweathers[0].getLow());
        temperature2.setText(futureweathers[1].getHigh()+"~"+futureweathers[1].getLow());
        temperature3.setText(futureweathers[2].getHigh()+"~"+futureweathers[2].getLow());
        temperature4.setText(futureweathers[3].getHigh()+"~"+futureweathers[3].getLow());


        weather1.setText(futureweathers[0].getWeather());
        weather2.setText(futureweathers[1].getWeather());
        weather3.setText(futureweathers[2].getWeather());
        weather4.setText(futureweathers[3].getWeather());

        fengli1.setText(futureweathers[0].getFengli());
        fengli2.setText(futureweathers[1].getFengli());
        fengli3.setText(futureweathers[2].getFengli());
        fengli4.setText(futureweathers[3].getFengli());



        mUpdateBtn.clearAnimation();
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

    }
    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        futureweathers = new futureweather[] {new futureweather(),new futureweather(),new futureweather(),new futureweather()};
        int flag = 0,i=0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");

                while (eventType != XmlPullParser.END_DOCUMENT ) {
                    switch (eventType) {
// 判断当前事件是否为文档开始事件
                        case XmlPullParser.START_DOCUMENT:
                            break;
// 判断当前事件是否为标签元素开始事件
                        case XmlPullParser.START_TAG:
                            if (xmlPullParser.getName().equals("resp")) {
                                todayWeather = new TodayWeather();
                            }
                            if (todayWeather != null) {
                                if (xmlPullParser.getName().equals("city")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setCity(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("updatetime")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setUpdatetime(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("shidu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setShidu(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("wendu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setWendu(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("pm25")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setPm25(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("quality")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setQuality(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                    fengxiangCount++;
                                } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli(xmlPullParser.getText());
                                    fengliCount++;
                                } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType(xmlPullParser.getText());
                                    typeCount++;
                                } else if(xmlPullParser.getName().equals("date")) {
                                    eventType = xmlPullParser.next();
                                    String tmp = xmlPullParser.getText();
                                    int index = tmp.indexOf("星");
                                    Log.d("index", index + "  " + i);
                                    futureweathers[i].setWeekday("星期" + tmp.charAt(index + 2));
                                    dateCount++;

                                } else if (xmlPullParser.getName().equals("high") ) {
                                    eventType = xmlPullParser.next();
                                    futureweathers[i].setHigh(xmlPullParser.getText().substring(2).trim());
                                    highCount++;

                                } else if (xmlPullParser.getName().equals("low") ) {
                                    eventType = xmlPullParser.next();
                                    futureweathers[i].setLow(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;

                                } else if (xmlPullParser.getName().equals("type")&&(typeCount==2||typeCount==4||typeCount==6||typeCount==8)) {
                                    eventType = xmlPullParser.next();
                                    futureweathers[i].setWeather(xmlPullParser.getText());
                                    typeCount++;

                                } else if (xmlPullParser.getName().equals("fengli") && (fengliCount==3||fengliCount==5||fengliCount==7||fengliCount==9)) {
                                    eventType = xmlPullParser.next();
                                    futureweathers[i].setFengli(xmlPullParser.getText());
                                    fengliCount++;
                                    i++;
                                }else if (xmlPullParser.getName().equals("fengli") ) {
                                    eventType = xmlPullParser.next();
                                    fengliCount++;
                                }else if (xmlPullParser.getName().equals("type") ) {
                                    eventType = xmlPullParser.next();
                                    typeCount++;
                                }
// 判断当前事件是否为标签元素结束事件
                            }
                                break;
                                case XmlPullParser.END_TAG:break;
                            }
                          eventType = xmlPullParser.next();
                    }
// 进入下一个元素并触发相应事件


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context context,Intent intent){
            queryWeatherCode(citycodeformain);

        }
    }
}

