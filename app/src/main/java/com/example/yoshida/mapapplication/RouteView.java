package com.example.yoshida.mapapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by yoshida on 2015/02/18.
 */
public class RouteView extends Activity{
    TextView tv ;
    MapsActivity ma;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routeview);
        tv=(TextView)findViewById(R.id.textView);
        tv.setText(ma.posinfo);
    }
}
