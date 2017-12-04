package com.hb.jensenhaw.suyuanapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by admin on 2017/4/7.
 */
public class SettingsActivity extends AppCompatActivity {

    private Button btnSet ;
    private EditText editIPSet ;
    private EditText editPortSet ;



    private int powerInt ;
    private int serialInt ;
    String power ;
    String serial;
    String ip ;
    String port;

    private String[] powers = new String[]{"3.3v", "5v", "scan power", "rfid power", "psam power"} ;
    private String[] serials = new String[]{"com0", "com2", "com3", "com11", "com12", "com13", "com14"} ;
    private ParaSave para ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(R.string.settings));
        para = new ParaSave(this);
        initView();
    }

    private void initView(){
        btnSet = (Button) findViewById(R.id.buttonSettings) ;
        editIPSet = (EditText) findViewById(R.id.editIP) ;
        editPortSet = (EditText) findViewById(R.id.editPort) ;

        editIPSet.setText(para.getIP());
        editPortSet.setText(para.getPort());

//        tvTips = (TextView) findViewById(R.id.textSettingTips) ;


        serial = para.getSerial() ;
        power = para.getPower() ;


        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                para.savePower(power);
                para.saveSerial(serial);
                ip = editIPSet.getText().toString();
                para.saveIP(ip);
                editIPSet.setText(ip);
                port = editPortSet.getText().toString();
                para.savePort(port);
                editPortSet.setText(port);
                Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

}
