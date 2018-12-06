package com.floridapoly.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPLocation extends AsyncTask<String, Void, LatLng> {

    private static final String TAG = "IPLocation";
    private static final String API_KEY = "e4d840e3af6d4f9cba42da70a8201100";


    protected LatLng doInBackground(String... ip) {
        try{
            URL url = new URL("https://api.ipgeolocation.io/ipgeo?apiKey="+API_KEY+"&ip=" + ip[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject myResponse = new JSONObject(response.toString());
            System.out.println("result after Reading JSON Response");
            double lat = Double.parseDouble(myResponse.getString("latitude"));
            double lon = Double.parseDouble(myResponse.getString("longitude"));
            System.out.println(lat);
            System.out.println(lon);
            LatLng point = new LatLng(lat,lon);
            return point;
        }
        catch(Exception e){
            Log.v(TAG, ""+e);
        }

        return null;
    }
}