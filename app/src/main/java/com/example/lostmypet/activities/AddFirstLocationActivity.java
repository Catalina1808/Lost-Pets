package com.example.lostmypet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.lostmypet.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.util.List;

public class AddFirstLocationActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener {


    long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private Point position;
    private SymbolManager symbolManager;
    private Button saveLocationButton;
    private SwitchCompat switchMapStyle;
    private Symbol symbol;
    private Location originLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_add_first_location);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        saveLocationButton = findViewById(R.id.start_button);
        switchMapStyle = findViewById(R.id.switchMapStyle);

        enableLocation();


        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position !=null) {
                    Intent intent = new Intent(AddFirstLocationActivity.this, AddAnnouncementActivity.class);
                    intent.putExtra("LATITUDE", position.latitude());
                    intent.putExtra("LONGITUDE", position.longitude());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddFirstLocationActivity.this, "You should select a place on the map.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        switchMapStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //declare a new Style.OnStyleLoaded to add the marker to the style
                Style.OnStyleLoaded styleLoaded= new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        symbolManager.setIconAllowOverlap(true);
                        style.addImage("myMarker", BitmapFactory.decodeResource(getResources(), R.drawable.paw_mark_map));
                    }
                };

                if(map!=null) {
                    if (isChecked) {
                        map.setStyle(Style.MAPBOX_STREETS, styleLoaded);
                        switchMapStyle.setText(getResources().getString(R.string.streets_view));
                        switchMapStyle.setTextColor(Color.BLACK);
                    }
                    else {
                        map.setStyle(Style.SATELLITE_STREETS, styleLoaded);
                        switchMapStyle.setText(getResources().getString(R.string.satellite_view));
                        switchMapStyle.setTextColor(Color.WHITE);
                    }
                }
            }
        });
    }

    //add first mark on map

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setIconAllowOverlap(true);
                style.addImage("myMarker", BitmapFactory.decodeResource(getResources(), R.drawable.paw_mark_map));
                if(position!=null) {
                    symbol = symbolManager.create(new SymbolOptions()
                            .withLatLng(new LatLng(position.latitude(), position.longitude()))
                            .withIconImage("myMarker"));
                }
            }
        });

        map = mapboxMap;
        if(position!=null) {
            setCameraPosition();
        }
        map.addOnMapClickListener(this);

    }



    @SuppressLint("MissingPermission")
    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get the user location

            //initialize local engine

            locationEngine = LocationEngineProvider.getBestLocationEngine(this);
            locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    Location lastLocation = result.getLastLocation();
                    if (lastLocation != null) {
                        originLocation = lastLocation;
                        position = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
                    } else {
                        Toast.makeText(AddFirstLocationActivity.this, "The current location could not be found.",
                                Toast.LENGTH_LONG).show();
                        //position = Point.fromLngLat(26.021101, 44.940918);
                    }
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(AddFirstLocationActivity.this, "The current location could not be found.",
                            Toast.LENGTH_LONG).show();
                }
            });

        }else {
            //Get a custom location if the user do not grant permissions for location
            Toast.makeText(AddFirstLocationActivity.this,
                    "The current location could not be found because the permission is not granted.",
                    Toast.LENGTH_LONG).show();
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    private void setCameraPosition(){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position.latitude(),
                position.longitude()), 12));
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        //delete the last marker if it was not set
        if(symbol!=null){
            symbolManager.delete(symbol);
        }

        symbol= symbolManager.create(new SymbolOptions()
                .withLatLng(point)
                .withIconImage("myMarker"));

        position = Point.fromLngLat(point.getLongitude(), point.getLatitude());

        saveLocationButton.setVisibility(View.VISIBLE);
        return false;
    }

    //if the permissions are not grated


//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//        Toast.makeText(this, "You should grant this permission to see your current location!",
//                Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//        if(granted){
//            enableLocation();
//        } else {
//            Toast.makeText(this, "You should grant this permission to see your current location!",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



}