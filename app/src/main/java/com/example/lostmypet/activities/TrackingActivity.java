package com.example.lostmypet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostmypet.DAO.DAOLocationPoint;
import com.example.lostmypet.R;
import com.example.lostmypet.models.LocationPoint;
import com.example.lostmypet.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
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
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class TrackingActivity extends AppCompatActivity implements NavigationRouterCallback, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {


    private static final String TAG = "";
    long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Point originPosition;
    private Point destinationPosition;
    private SymbolManager symbolManager;
    private Button saveLocationButton;
    private Button deleteLocationButton;
    private SwitchCompat switchMapStyle;
    private MapboxNavigation mapboxNavigation;
    private NavigationMapRoute navigationMapRoute;
    private List<Point> coordonates;
    private Symbol symbol;
    private boolean pressedButton;
    private Location originLocation;

    private ArrayList<LocationPoint> locationPoints;
    private String announcementID;
    private FirebaseUser currentUser;
    private DAOLocationPoint daoLocationPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init the map
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_tracking);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Get the firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //get the data acces object for locationPoint
        daoLocationPoint = new DAOLocationPoint();

        //get UI elements
        saveLocationButton = findViewById(R.id.btn_add_location);
        deleteLocationButton = findViewById(R.id.btn_delete_locations);
        switchMapStyle = findViewById(R.id.switchMapStyle);

        //get announcementID from the sender activity
        Intent intent = getIntent();
        announcementID = intent.getStringExtra("announcementID");


        coordonates = new ArrayList<>();
        locationPoints = new ArrayList<>();
        getLocations();
        //enableLocation();

        pressedButton = false;

        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressedButton = true;
                coordonates.add(destinationPosition);
                makeRoutes();
                addLocationPoint(destinationPosition.latitude(), destinationPosition.longitude());

            }
        });

        deleteLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLocationPoints();
            }
        });

        switchMapStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //declare a new Style.OnStyleLoaded to add the marker to the style
                Style.OnStyleLoaded styleLoaded = new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        symbolManager.setIconAllowOverlap(true);
                        style.addImage("myMarker", BitmapFactory.decodeResource(getResources(), R.drawable.paw_mark_map));
                    }
                };

                //set map style
                if (map != null) {
                    if (isChecked) {
                        map.setStyle(Style.MAPBOX_STREETS, styleLoaded);
                        switchMapStyle.setText("Streets view");
                        switchMapStyle.setTextColor(Color.BLACK);
                    } else {
                        map.setStyle(Style.SATELLITE_STREETS, styleLoaded);
                        switchMapStyle.setText("Satellite view");
                        switchMapStyle.setTextColor(Color.WHITE);
                    }
                    addMarkersOnMap();
                }
            }
        });
    }

    private void makeRoutes(){
        if(coordonates.size()>=2) {
            if (mapboxNavigation != null) {
                mapboxNavigation.onDestroy();
            }
            RouteOptions routeOptions = RouteOptions.builder().coordinatesList(coordonates).profile(DirectionsCriteria.PROFILE_DRIVING).user("mapbox").geometries("polyline6").steps(true).build();
            NavigationOptions navigationOptions = new NavigationOptions.Builder(TrackingActivity.this).accessToken(getString(R.string.mapbox_access_token)).build();
            mapboxNavigation = new MapboxNavigation(navigationOptions);
            mapboxNavigation.requestRoutes(routeOptions, TrackingActivity.this);
        }

    }


    private void addLocationPoint(double latitude, double longitude){
        LocationPoint locationPoint = new LocationPoint(latitude, longitude, announcementID,
                currentUser.getUid());
        daoLocationPoint.add(locationPoint).
                addOnSuccessListener(succes -> Toast.makeText(getApplicationContext(),
                        "Location point inserted",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(err -> Toast.makeText(getApplicationContext(),
                        "Insertion failed",
                        Toast.LENGTH_SHORT).show());
    }

    private void removeLocationPoints(){
        for(LocationPoint locationPoint: locationPoints) {
            if (locationPoint.getUserID().equals(currentUser.getUid())) {
                daoLocationPoint.remove(locationPoint.getLocationPointID()).
                        addOnSuccessListener(succes -> Toast.makeText(getApplicationContext(),
                                "Location points removed",
                                Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(err -> Toast.makeText(getApplicationContext(),
                                "Removal failed",
                                Toast.LENGTH_SHORT).show());
            }
        }
//        symbolManager.deleteAll();
//        addMarkersOnMap();
//        makeRoutes();
    }


    public void getLocations(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

        DatabaseReference databaseReferenceLocations = database
                .getReference(LocationPoint.class.getSimpleName());

        databaseReferenceLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coordonates.clear();
                locationPoints.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        LocationPoint locationPoint = dataSnapshot.getValue(LocationPoint.class);
                        if(Objects.equals(Objects.requireNonNull(locationPoint).getAnnouncementID(),
                                announcementID)) {
                            locationPoints.add(locationPoint);
                            coordonates.add(Point.fromLngLat(Objects.requireNonNull(locationPoint).getLongitude(),
                                    locationPoint.getLatitude()));
                        }

                }
                makeRoutes();//???
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //add markers on map after changing map style
    private void addMarkersOnMap() {

        for (Point point : coordonates) {
            symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(point.latitude(), point.longitude()))
                    .withIconImage("myMarker"));
        }
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
//                symbolManager.create(new SymbolOptions()
//                        .withLatLng(new LatLng(originPosition.latitude(), originPosition.longitude()))
//                        .withIconImage("myMarker"));
                addMarkersOnMap();
                makeRoutes();
            }
        });

        map = mapboxMap;
        if(!coordonates.isEmpty())
            setCameraPosition();
        map.addOnMapClickListener(this);

    }




//    @SuppressLint({"MissingPermission", "WrongConstant"})
//    private void initializeLocationLayer(){
//        locationLayerPlugin= new LocationLayerPlugin(mapView, map, locationEngine);
//        locationLayerPlugin.setLocationLayerEnabled(true);
//        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
//        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setCameraPosition() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordonates.get(0).latitude(),
                coordonates.get(0).longitude()), 12));
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        //delete the last marker if it was not set
        if (symbol != null && !pressedButton) {
            symbolManager.delete(symbol);
        }

        pressedButton = false;
        symbol = symbolManager.create(new SymbolOptions()
                .withLatLng(point)
                .withIconImage("myMarker"));
//        if(destinationPosition!=null)
//            originPosition= destinationPosition;

        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());

        saveLocationButton.setVisibility(View.VISIBLE);
        return false;
    }

    //if the permissions are not grated
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "You should grant this permission to see your current location!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

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
        // mapboxNavigation.stopTripSession();
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
        if (mapboxNavigation != null) {
            mapboxNavigation.onDestroy();
        }
        mapView.onDestroy();
    }


    @Override
    public void onCanceled(@NotNull RouteOptions routeOptions, @NotNull RouterOrigin routerOrigin) {
        Timber.e("Canceled!");
    }

    @Override
    public void onFailure(@NotNull List<RouterFailure> list, @NotNull RouteOptions routeOptions) {
        Timber.e("Failure!");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRoutesReady(@NotNull List<NavigationRoute> list, @NotNull RouterOrigin routerOrigin) {

//        mapboxNavigation.setNavigationRoutes(list);
//        mapboxNavigation.startTripSession();

        DirectionsRoute route = list.get(0).getDirectionsRoute();

        if (list == null) {
            Timber.e("No routes found, check right user and access token");
            return;
        } else if (list.size() == 0) {
            Timber.e("No routes found");
            return;
        }

        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        } else {
            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
        }

        navigationMapRoute.addRoute(route);

//        NavigationLauncherOptions options= NavigationLauncherOptions.builder()
//                .directionsRoute(route)
//                .shouldSimulateRoute(false)
//                .build();
//        NavigationLauncher.startNavigation(MainActivity.this, options);


    }

}