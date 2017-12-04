package com.hb.jensenhaw.suyuanapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogisticsActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.activity_logistics,
                new String[]{"title","status","regionname","operator","operTime","titleView"},
                new int[]{R.id.title,R.id.status,R.id.regionname,R.id.operator,R.id.operTime,R.id.titleView});
        setListAdapter(adapter);
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
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Bundle bundle=getIntent().getExtras();
        int jieDuan;
        jieDuan =  bundle.getInt("i");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title",bundle.getString("name0") );
        map.put("status",bundle.getString("status0"));
        map.put("regionname", bundle.getString("regionname0"));
        map.put("operator",bundle.getString("operator0"));
        map.put("operTime", bundle.getString("operTime0"));
        map.put("titleView","第"+ jieDuan +"阶段");
        list.add(map);

        for (int i = 1;i < bundle.getInt("i"); i++) {
            jieDuan =  bundle.getInt("i") - i;
            map = new HashMap<String, Object>();
            map.put("title", bundle.getString("name"+i));
            map.put("status", bundle.getString("status"+i));
            map.put("regionname", bundle.getString("regionname"+i));
            map.put("operator", bundle.getString("operator"+i));
            map.put("operTime", bundle.getString("operTime"+i));
            map.put("titleView","第"+ jieDuan +"阶段");
            list.add(map);
        }
        return list;
    }
}
