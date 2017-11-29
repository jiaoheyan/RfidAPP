package com.hb.jensenhaw.rfidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_QR_CODE = 1;
    private Button rfidBtn;
    private Button enMoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rfidBtn = (Button)findViewById(R.id.rfidBtn);
        rfidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,UHFActivity.class);
                startActivity(intent);
            }
        });
        enMoBtn=(Button)findViewById(R.id.enMoBtn);
        enMoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent=new Intent(MainActivity.this,EnMoActivity.class);
                startActivity(intent);
            }
        });
        final Button qrCodeBtn = (Button) findViewById(R.id.qrBtn);
        qrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SimpleCaptureActivity.class);
                MainActivity.this.startActivityForResult(i, REQUEST_QR_CODE);
            }
        });
        final Button settings = (Button) findViewById(R.id.setBtn);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sintent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(sintent);
            }
        });

        final Button agvBtn = (Button) findViewById(R.id.logistcsBtn);
        agvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agv = new Intent(MainActivity.this, AGVActivity.class);
                startActivity(agv);
            }
        });

        final Button vision = (Button) findViewById(R.id.visionBtn);
        vision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createDialog();
                try {
                    PackageManager packageManager = getPackageManager();
                    Intent vintent=new Intent();
                    vintent = packageManager.getLaunchIntentForPackage("com.cctv.keye");
                    startActivity(vintent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent viewIntent = new
                            Intent("android.intent.action.VIEW", Uri.parse("http://weixin.qq.com/"));
                    startActivity(viewIntent);
                }
            }
        });

        final Button exit = (Button) findViewById(R.id.exitBtn);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);


            }
        });
    }
    /**
     * create dialog for view about soft information
     */
    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);

      //  AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        //version code + finish date
        String info = "天津市翰本科技有限责任公司"+ "\n"+ getResources().getString(R.string.current_version) + getVersion() + "\n"
                + getResources().getString(R.string.finish_date) + "2017-09-07";
        builder.setTitle(getResources().getString(R.string.about_soft)) ;
        builder.setMessage(info) ;
        builder.setNegativeButton(getResources().getString(R.string.ok), null) ;
        builder.setIcon(R.drawable.logo1);
        builder.create().show();

//        LayoutInflater inflater = getLayoutInflater();
//        View   dialog = inflater.inflate(R.layout.dialog,(ViewGroup) findViewById(R.id.dialog));
//        EditText editText = (EditText) dialog.findViewById(R.id.et);


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("关于");
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Toast.makeText(MainActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        builder.setView(dialog);
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.show();


    }

    /**
     * get current version
     * @return
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return "" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
