package com.floridapoly.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.VpnService;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {


    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    HeatmapTileProvider mProvider;
    TileOverlay mOverlay;
    private List<MarkerOptions> markerList = new ArrayList<>();
    private List<LatLng> heatList = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button backBtn = (Button) findViewById(R.id.main_btn_back);
        Button markerBtn = findViewById(R.id.main_btn_marker);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, testActivity.class));
            }
        });
        ////////////
        markerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(50, 50)).title("^;..;^"));
            }
        });
///////////////////////////////////
/*
        JSONWrite JSWriter = new JSONWrite(this);
        try {
            JSWriter.WriteToFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JSONRead JSRead = new JSONRead(this);
        try {
            JSRead.ReadFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String path = this.getFilesDir().getAbsolutePath() + "/NickD";

        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
        */
///////////////////////////////////

///////////////////////////////////////////////
        Log.d("Map", "No map");
        getLocationPermission();

///////////////////////////////////////////////


    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initMap() {
        Log.d(TAG, "initmap: init");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        Log.d("Map", "Call Google");

        mapFragment.getMapAsync(MainActivity.this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////GoogleMaps//////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show();

        Log.d("Map", "Please Google");

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);



        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d("Map", "Map Loaded");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /////////////////////////////////////////////////////////////////
                        // Create a heat map tile provider, passing it the latlngs of the police stations.
                        PacketReader reader = new PacketReader(MainActivity.this);
                        try {
                            reader.readPacketFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        LatLng initGeoTag = reader.listOfLatLng.get(0);
                        heatList.add(initGeoTag);
                        mProvider = new HeatmapTileProvider.Builder()
                                .data(heatList)

                                .build();
                        // Add a tile overlay to the map, using the heat map tile provider.
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TileOverlayOptions mTile = new TileOverlayOptions().tileProvider(mProvider);

                                if(mOverlay == null){
                                    Log.d("Map", "overlay null");
                                }


                                mOverlay = mMap.addTileOverlay(mTile);
                                if(mOverlay != null){
                                    Log.d("Map", "overlay not null");
                                }

                                Log.d("Map", "HeatMap Generated");
                            }
                        });
                        for(int i =1; i < reader.listOfLatLng.size(); i++){

                            LatLng geoTag = reader.listOfLatLng.get(i);
                            //HeatMap smack
                            heatList.add(geoTag);
                            mProvider.setData(heatList);
                            if (mOverlay != null && (i%10 == 0)) {
                                updateHeatMapOverlay(mOverlay);
                            }
                            Log.d("Map", "HeatMap Updated");


                            //Heatmap end
                            
                            MarkerOptions tag = new MarkerOptions().position(geoTag);
                            markerList.add(tag);

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        /*
                        for(MarkerOptions options: markerList){
                            //mMap.addMarker(options);
                            //Replace this with ANOTHER THREAD BECAUSE WHO KNOWS?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? :^}
                            updateGoogleMap(options);
                            Log.d("Map", "Marker Added");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }*/
                        ///////////////////

                        /////////////////////////////////////////////////////////////////
                    }
                }).start();



            }
        });
    }


////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location perms");

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        Log.d(TAG, "getLocationPermission: array init");

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "getLocationPermission: first if");

            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "getLocationPermission: second if");

                mLocationPermissionGranted = true;
                Log.d(TAG, "getLocationPermission: permissions already given");
                initMap();
            }else{
                Log.d(TAG, "getLocationPermission: gonna ask");

                ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            Log.d(TAG, "getLocationPermission: gonna ask 1st");
            ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //Everything is coo
            Log.d(TAG, "isServicesOK: Services is working");

        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: ResolvableError");
        }else{
            Toast.makeText(this, "Can't make map", Toast.LENGTH_LONG).show();
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");

        mLocationPermissionGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 ){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////
    public void updateGoogleMap(final MarkerOptions option){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.addMarker(option);
            }
        });
    }

    public void updateHeatMapOverlay(final TileOverlay overlay){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                overlay.clearTileCache();
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {


        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
