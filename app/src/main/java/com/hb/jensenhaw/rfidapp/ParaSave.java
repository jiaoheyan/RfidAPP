package com.hb.jensenhaw.rfidapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2017/4/7.
 */
public class ParaSave {

    private Context context ;

    public ParaSave(Context context) {
        this.context = context ;
    }

    public void saveSerial(String serial){
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = shared.edit() ;
        editor.putString("serial", serial) ;
        editor.commit() ;

    }

    public String getSerial() {
        String serial = "" ;
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        serial = shared.getString("serial", "com13") ;
        return serial ;
    }


    public void savePower(String power){
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = shared.edit() ;
        editor.putString("power", power) ;
        editor.commit() ;

    }

    public String getPower() {
        String power = "" ;
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        power = shared.getString("power", "5v") ;
        return power ;
    }

    public void saveIP(String ip){
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = shared.edit() ;
        editor.putString("ip", ip) ;
        editor.commit() ;

    }

    public String getIP() {
        String ip = "" ;
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        ip = shared.getString("ip", "192.168.1.100") ;
        return ip ;
    }

    public void savePort(String port){
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = shared.edit() ;
        editor.putString("port", port) ;
        editor.commit() ;

    }

    public String getPort() {
        String port = "" ;
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE) ;
        port = shared.getString("port", "8080") ;
        return port ;
    }
}
