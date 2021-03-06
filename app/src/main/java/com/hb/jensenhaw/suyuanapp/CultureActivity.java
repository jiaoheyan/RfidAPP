package com.hb.jensenhaw.suyuanapp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class CultureActivity extends AppCompatActivity {

    public static final int SHOW_RESPONSE = 0;
    private final static String TAG = CultureActivity.class.getSimpleName();
    PlantView myView;
    private ParaSave para ;
    private GoogleApiClient client;
    Timer mTimer;
    TextView oneTv,twoTv,threeTv,fourTv,fiveTv;
    String cultureWD1 = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        myView = (PlantView)findViewById(R.id.mv);
        para = new ParaSave(this) ;
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        oneTv=(TextView) findViewById(R.id.oneTv);
        twoTv=(TextView) findViewById(R.id.twoTv);
        threeTv=(TextView) findViewById(R.id.threeTv);
        fourTv=(TextView) findViewById(R.id.fourTv);
        fiveTv=(TextView) findViewById(R.id.fiveTv);

        sendRequestWithHttpClient();
        mTimer = new Timer();
        mTimer.schedule(task, 1000,1000);
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
                    String yzarea="";
                    String[] ss = new String[8];
                    String cultureWendu="";
                    String cultureShidu="";
                    String culturegas="";
                    String cultureFire="";
                    String cultureShexiangtou="";
                    String cultureWD;

                    try {
                        JSONObject kuqucanshu  = new JSONObject(response.toString());
                        yzarea = kuqucanshu.getString("yangzhi");
                        ss = yzarea.split(",");
                        cultureWendu = ss[0];
                        cultureShidu = ss[1];
                        culturegas = ss[2];
                        cultureFire = ss[3];
                        cultureShexiangtou = ss[4];
                        cultureWD = ss[5];
                        oneTv.setText(cultureWendu);
                        twoTv.setText(cultureShidu);
                        threeTv.setText(culturegas);
                        fourTv.setText(cultureFire);
                        fiveTv.setText(cultureShexiangtou);
                        if (cultureWD.equals(cultureWD1)){

                        }
                        else {
                            myView.change(Float.parseFloat(cultureWD));
                            myView.moveWaterLine();
                        }
                        cultureWD1 = cultureWD;
                        Log.e(TAG, "handleMessageqqq: "+cultureWendu+cultureShidu+culturegas+cultureFire+cultureShexiangtou );
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
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
    private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                Log.e(TAG, "run:cultureActivity " );
                Log.e(TAG, para.getIP() );

                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址
                HttpGet httpGet = new HttpGet("http://" + para.getIP()+":"+para.getPort()+"/openAPI/getTemp.do");
//                Log.e(TAG,"http://" + para.getIP()+":"+para.getPort()+"/openAPI/getTemp.do");
                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response.toString();
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();//这个start()方法不要忘记了
    }
}
