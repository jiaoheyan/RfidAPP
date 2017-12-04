package com.hb.jensenhaw.rfidapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RFIDDetailActivity extends AppCompatActivity {
    private final static String TAG = RFIDDetailActivity.class.getSimpleName();
    protected Activity mActivity = this;
    public static final int SHOW_RESPONSE = 0;
    private ParaSave para ;

    private GoogleApiClient client;
    // private ParaSave para ;
    public String str;
    public String code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        para = new ParaSave(this) ;
        Bundle bundle=getIntent().getExtras();
        code=bundle.getString("message");
        sendRequestWithHttpClient();
        }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_RESPONSE:
                    JSONArray jsonArray;
                    Bundle bundle = new Bundle();
                    //String name;
                    try {
                        String s1 = msg.obj.toString();
                        String s2 = s1.substring(1,msg.obj.toString().length()-1).trim();
                        // jsonObject = new JSONObject(s2);
                        jsonArray =new JSONArray(s1);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = (JSONObject) jsonArray.get(i);
                            System.out.println(temp.getString("name"));
                            bundle.putString("name"+i,temp.getString("name"));
                            bundle.putString("status"+i,temp.getString("status"));
                            bundle.putString("regionname"+i,temp.getString("regionname"));
                            bundle.putString("operator"+i,temp.getString("operator"));
                            bundle.putString("operTime"+i,temp.getString("operTime"));
                        }
                        bundle.putInt("i",jsonArray.length());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    bundle.putString("danhao", code);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(RFIDDetailActivity.this, LogisticsActivity.class);
                    startActivity(intent);
                    RFIDDetailActivity.this.finish();
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
                if (!code.equals("")) {
                    //                  HttpGet httpGet = new HttpGet("http://192.168.1.117:8080/openAPI/selectBydh.do?danhao=" + code);
                    HttpGet httpGet = new HttpGet("http://" + para.getIP()+":"+para.getPort()+"/openAPI/selectBycode.do?code=" + code);
//                    HttpGet httpGet = new HttpGet("http://www.baidu.com");
//                    Log.e(TAG," http://192.168.1.112:8080/openAPI/updateGuanying.do?code=" + code) ;
                    Log.e(TAG,"http://" + para.getIP()+":"+para.getPort()+"/openAPI/selectBydh.do?danhao=" + code);
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
                            message.obj = response;
                            System.out.println(message);
                            handler.sendMessage(message);

                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("error");
                }
            }
        }).start();//这个start()方法不要忘记了

    }
}
