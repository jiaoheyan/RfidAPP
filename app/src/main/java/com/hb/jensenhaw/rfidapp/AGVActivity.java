package com.hb.jensenhaw.rfidapp;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class AGVActivity extends AppCompatActivity {
    private OutputStream outputStream = null;
    private Socket socket = null;
    private byte[] Data;
    private boolean socketStatus = false;
    private int agvName = 0;
    TextView agvTv;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agv);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        agvTv = (TextView)findViewById(R.id.agvName);

        image = (ImageView) findViewById(R.id.Image);

        image.setImageDrawable(getResources().getDrawable(R.drawable.car));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void connect(View view){

        String wifiName4 = "677C";
        String wifiName5 = "6608";

        try
        {
            String s1 = new String(getConnectWifiSsid().getBytes(),"utf-8");
            String s2 = new String(wifiName4.getBytes(),"utf-8");
            String s3 = new String(wifiName5.getBytes(),"utf-8");
            if (s1.indexOf(s2) != -1){
                agvName = 1;
                agvTv.setText("冷链物流");
            }
            if (s1.indexOf(s3) != -1){
                agvName = 2;
                agvTv.setText("食品溯源");
            }
            if (agvName == 1|| agvName == 2){

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        if (!socketStatus) {

                            try {
                                socket = new Socket("10.10.100.254",8899);
                                if(socket == null){
                                }else {
                                    socketStatus = true;
                                }
                                outputStream = socket.getOutputStream();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
            }
            else {
                Toast.makeText(AGVActivity.this,"您连接wifi错误，请重新选择",Toast.LENGTH_LONG).show();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void wifi(View view){
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        startActivity(intent);
    }

    private String getConnectWifiSsid(){
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }
    public void sendForward(View view){
        image.setImageDrawable(getResources().getDrawable(R.drawable.car));
        Animation zhangch;
        zhangch = new TranslateAnimation(380f,-380f,10f,10f);
        zhangch.setDuration(2000);
        zhangch.setFillAfter(true);
        zhangch.setRepeatCount(-1);
        image.startAnimation(zhangch);

        Data = dataProcess((byte)0x01);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(socketStatus){
                    try {
                        for (int i=0;i<Data.length;i++){
                            outputStream.write(Data[i]);
                        }
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    public void sendBack(View view){

        Animation zhangch;
        zhangch = new TranslateAnimation(-380f,380f,10f,10f);
        zhangch.setDuration(2000);
        zhangch.setFillAfter(true);
        zhangch.setRepeatCount(-1);
        image.startAnimation(zhangch);

        Data = dataProcess((byte)0x02);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(socketStatus){
                    try {
                        for (int i=0;i<Data.length;i++){
                            outputStream.write(Data[i]);
                        }
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    public void sendStop(View view){

        Animation zhangch;
        zhangch = new ScaleAnimation(1.0f,1.0f,1.0f,1.0f);
        zhangch.setDuration(2000);
        zhangch.setFillAfter(true);
        image.startAnimation(zhangch);

        Data = dataProcess((byte)0x03);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(socketStatus){
                    try {
                        for (int i=0;i<Data.length;i++){
                            outputStream.write(Data[i]);
                        }
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    private byte[] dataProcess(byte direction){
        byte[] bytes = new byte[13];
        bytes[0] = (byte)0xFC;
        bytes[1] = (byte)agvName;
        bytes[2] = (byte)0x01;
        bytes[3] = direction;
        bytes[4] = (byte)0x00;
        bytes[5] = (byte)0x00;
        bytes[6] = (byte)0x00;
        bytes[7] = (byte)0x00;
        bytes[8] = (byte)0x00;
        bytes[9] = (byte)0x00;
        bytes[10] = (byte)0x00;
        bytes[11] = (byte)0x00;
        bytes[12] = (byte)0xDF;
        return bytes;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*关闭相应的资源*/
        try {
            if (socketStatus){
                socket = null;
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            if (socketStatus){
                socket = null;
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}