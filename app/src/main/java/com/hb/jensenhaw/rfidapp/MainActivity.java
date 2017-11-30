package com.hb.jensenhaw.rfidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_QR_CODE = 1;
    private Button rfidBtn;
    private Button enMoBtn;
    private ViewPager vpGuide;
    private LinearLayout llpointgroup;
    private View viewRedPoint;
    private int mPointWidth;
    private static final int[] mImageIds = new int[]{R.drawable.guideo1,R.drawable.guidetw1,
            R.drawable.guideth1};
    private ArrayList<ImageView> mImageViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpGuide = (ViewPager) findViewById(R.id.vp_guide);
        llpointgroup = (LinearLayout) findViewById(R.id.ll_point_group);
        viewRedPoint = findViewById(R.id.viewRedPoint);
        initView();
        vpGuide.setAdapter(new GuideAdapter());
        vpGuide.setOnPageChangeListener(new GuidePageListener());

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
    private void initView(){
        mImageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(image);
        }
        for (int i = 0; i < mImageIds.length; i++) {
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10,10);
            if(i > 0)
            {
                params.leftMargin = 10;
            }
            point.setLayoutParams(params);
            llpointgroup.addView(point);
        }
        llpointgroup.getViewTreeObserver().addOnGlobalLayoutListener
                (new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        llpointgroup.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        mPointWidth = llpointgroup.getChildAt(1).getLeft() - llpointgroup.getChildAt(0).getLeft();
                    }
                });
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViewList.get(position));
            return mImageViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    class GuidePageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            System.out.println("position"+position+"positionoffset"+positionOffset
                    +"positionoffsetpixels"+positionOffsetPixels);
            int len = (int) (mPointWidth*positionOffset)+mPointWidth*position;
            RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) viewRedPoint.getLayoutParams();
            params.leftMargin = len;
            viewRedPoint.setLayoutParams(params);
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub

        }
    }
}

