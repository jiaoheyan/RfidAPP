package com.hb.jensenhaw.rfidapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SensorActivity extends AppCompatActivity {
    public static final int SHOW_RESPONSE = 0;
    private final static String TAG = SensorActivity.class.getSimpleName();
    private TextView mBJWenduView;
    private TextView mBJShiduView;
    private TextView mBJGasView;
    private TextView mBJLightView;
    private TextView mBJManView;
    public String cangku;
    private ParaSave para ;
    private GoogleApiClient client;
    String light;
    String status;
    Timer mTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        para = new ParaSave(this) ;
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mBJWenduView=(TextView) findViewById(R.id.bjwenduView);
        mBJShiduView=(TextView) findViewById(R.id.bjshiduView);
        mBJGasView=(TextView) findViewById(R.id.bjgasView);
        mBJLightView=(TextView) findViewById(R.id.bjlightView);
        mBJManView=(TextView) findViewById(R.id.bjmanView);

        cangku = "123";
        sendRequestWithHttpClient();
        mTimer = new Timer();
        mTimer.schedule(task, 1000,1000);

        final Button settings = (Button) findViewById(R.id.button2);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                light = "3" ;
                status = "0" ;

                sendRequestLightWithHttpClient();
//                Intent sintent = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(sintent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel timer
        mTimer.cancel();
    }
    TimerTask task= new TimerTask() {
        @Override
        public void run() {
            sendRequestWithHttpClient();
        }
    };
    //新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    Log.e(TAG, "handleMessage: "+response );
                    String bjarea="";
                    String[] ss = new String[5];
                    String bjwendu="";
                    String bjshidu="";
                    String bjgas="";
                    String bjlight="";
                    String bjman="";
                    String tjarea="";
                    String wqarea="";
                    String agvarea="";

                    try {
                        JSONObject kuqucanshu  = new JSONObject(response.toString());
//                        JSONArray infArray = product.getJSONArray("picurls");
                        bjarea = kuqucanshu.getString("bj");
                        ss = bjarea.split(",");
                        bjwendu = ss[0];
                        bjshidu = ss[1];
                        bjgas = ss[2];
                        bjlight = ss[3];
                        bjman = ss[4];
                        mBJWenduView.setText(bjwendu);
                        mBJShiduView.setText(bjshidu);
                        mBJGasView.setText(bjgas);
                        mBJLightView.setText(bjlight);
                        mBJManView.setText(bjman);
                        Log.e(TAG, "handleMessageqqq: "+bjwendu+bjshidu+bjgas+bjlight+bjman );
                        //desc = product.getString("description");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }

    };
    //方法：发送网络请求，获取百度首页的数据。在里面开启线程
    private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                Log.e(TAG, "run:111111111 " );
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址
                if (!cangku.equals("")) {
                   // HttpGet httpGet = new HttpGet("http://192.168.1.117:8080/openAPI/gettemperature.do");
                      HttpGet httpGet = new HttpGet("http://" + para.getIP()+":"+para.getPort()+"/openAPI/gettemperature.do");

                    Log.e(TAG,"http://" + para.getIP()+":"+para.getPort()+"/openAPI/gettemperature.do");
//                    try {
//                        //第三步：执行请求，获取服务器发还的相应对象
//                        HttpResponse httpResponse = httpCient.execute(httpGet);
//                        //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
//                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                            //第五步：从相应对象当中取出数据，放到entity当中
//                            HttpEntity entity = httpResponse.getEntity();
//                            String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
//
//                            //在子线程中将Message对象发出去
//                            Message message = new Message();
//                            message.what = SHOW_RESPONSE;
//                            message.obj = response.toString();
//                            handler.sendMessage(message);
//                        }
//
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }

                }
            }
        }).start();//这个start()方法不要忘记了

    }
    private Handler lighthandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    Log.e(TAG, "handleMessagelight: "+response );
                    String origaddr="";
                    try {
                        JSONObject wuliudetail = new JSONObject(response.toString());
//                        JSONArray infArray = product.getJSONArray("picurls");
                        origaddr = wuliudetail.getString("origaddr");
                        //desc = product.getString("description");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }

    };
    private void sendRequestLightWithHttpClient() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpClient httpCient = new DefaultHttpClient();
               // HttpGet httpGet = new HttpGet("http://192.168.1.117:8080/openAPI/selectBydh.do?danhao=" + code);
                HttpGet httpGet = new HttpGet("http://" + para.getIP()+":"+para.getPort()+"/openAPI/setlight.do?lightorder="+light );
                Log.e(TAG,"http://" + para.getIP()+":"+para.getPort()+"/openAPI/updateGuanying.do?code=" + cangku);
                    try {
                        HttpResponse httpResponse = httpCient.execute(httpGet);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            HttpEntity entity = httpResponse.getEntity();
                            String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串

                            Message message = new Message();
                            message.what = SHOW_RESPONSE;
                            message.obj = response.toString();
                            lighthandler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
        }).start();//这个start()方法不要忘记了

    }
}
