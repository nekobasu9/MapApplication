package com.example.yoshida.mapapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static String posinfo = "";
    public static String info_A = "";
    public static String info_B = "";
    public static  String test;
    public static String testtest;


    /*test
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        Button button01 = (Button)findViewById(R.id.button01);
        button01.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v){
                routeSearch();
                //showToast();
            }
        });
        Button button02 = (Button)findViewById(R.id.button02);
        button02.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //showToast();
                showRouteView();
            }
        });

    }
    /*void showToast(){
        Toast.makeText(this,posinfo,Toast.LENGTH_LONG).show();
    }*/
    void showRouteView(){
        Intent intent = new Intent();
        intent.setClassName("com.example.yoshida.mapapplication","com.example.yoshida.mapapplication.RouteView");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(35.681382, 139.766084)).title("Marker"));
    }
//routeを検索
    private void routeSearch(){
        LatLng origin = new LatLng(35.681382,139.766084);
        LatLng dest = new LatLng(35.781382,139.866084);

        String url = getDirectionsUrl(origin,dest);

        DownloadTask downloadTask = new DownloadTask();
        Log.d(url,"geturl");
        downloadTask.execute(url);


    }
    //directionsに渡すUrlの設定
    private String getDirectionsUrl(LatLng origin,LatLng dest){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        //パラメータ  渡す値をまとめる
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&language=ja"+"&mode=";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    private String downloadUrl(String strUrl) throws IOException{
        String data ="";
        InputStream iStream =null ;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null){
                sb.append(line);
                Log.d(line,line);
            }
            data = sb.toString();
            Log.d(data,data);
            br.close();
        }catch(Exception e) {
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... url){
            String data = "";
            try {
                data = downloadUrl(url[0]);
            }catch(Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        //doInBackground();
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    /*parse the  Google Place in JSON format*/
    private class ParserTask extends AsyncTask<String , Integer , List<List<HashMap<String,String>>>>{
        @Override
        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData){
            JSONObject jObject;
            List<List<HashMap<String,String>>> routes =null;
            try {
                jObject = new JSONObject(jsonData[0]);
                parseJsonOfDirectionAPI parser = new parseJsonOfDirectionAPI();

                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;

        }
        @Override
        protected void onPostExecute(List<List<HashMap<String,String>>> result){
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            if (result.size() != 0){
                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String,String>> path = result.get(i);

                    for (int j = 0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat,lng);

                        points.add(position);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(0x550000ff);

                }
                mMap.addPolyline(lineOptions);

            }else{
                mMap.clear();
                Toast.makeText(MapsActivity.this,"ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
            }

        }
    }
}
