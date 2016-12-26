package com.example.administrator.miniweather.bean;

import android.util.Log;

/**
 * Created by Andrew on 2016/11/30.
 */

public class futureweather {
    private String weekday;
    private String high;
    private String low;
    private String weather;
    private String fengli;

    public futureweather(){}

    public String getWeekday() {
        return weekday;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getWeather() {
        return weather;
    }

    public String getFengli() {
        return fengli;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }
    public void Show(){
        Log.d("show",weekday+high+low+weather+fengli);
    }

}
