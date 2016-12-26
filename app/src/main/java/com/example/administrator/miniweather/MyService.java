package com.example.administrator.miniweather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Andrew on 2016/11/16.
 */

public class MyService extends Service {
    private Intent intent = new Intent("updatedata");

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                   while (true) {
                       Thread.sleep(1000000);
                       sendBroadcast(intent);


                   }
                    }catch (Exception e)
                   {
                       e.printStackTrace();
                   }
               }


        }).start();
    }
    @Override
    public int onStartCommand (Intent intent,int flags, int startId){
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
