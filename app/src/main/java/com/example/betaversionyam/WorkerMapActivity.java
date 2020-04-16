package com.example.betaversionyam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static com.example.betaversionyam.FBref.refDis;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		28/03/2020
 *
 * this screen displays the area of the current distribution and the current location of the worker.
 */
public class WorkerMapActivity extends AppCompatActivity implements OnMapReadyCallback , LocationListener {
    LocationManager locationManager;
    Location mlocation;
    Intent t;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap gMap;
    ArrayList<LatAndLng> latAndLngArrayList;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    ArrayList<Marker> markerArrayList = new ArrayList<>();
    Polygon polygon;
    int i = 0;
    boolean toMove = true, isCurrenLoc = false;
    String name, workerName;

    /**
     * the function makes a connection between the variables in java to the xml components,
     * initialize the map, asks for permissions and find the current location.
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_map);
        t=getIntent();

        Toast.makeText(this, "it may take a few seconds", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            Toast.makeText(this, "you must allow the permission", Toast.LENGTH_SHORT).show();
            return;
        }

        else{
            SupportMapFragment supportMapFragment = (SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.workerMap);
            supportMapFragment.getMapAsync(WorkerMapActivity.this);
        }



        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mlocation=location;
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationServices.getFusedLocationProviderClient(WorkerMapActivity.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mlocation=location;
            }
        });
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(this);
        if (isCurrenLoc)
            refDis.child(name).child("currentLocation").child(workerName).removeValue();
        super.onPause();
    }

    /**
     * this function is called when the map is ready.
     * the function creates the same polygon as the manager created.
     * @param googleMap the displayed map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.moveCamera(CameraUpdateFactory.zoomBy(16));

        latAndLngArrayList = (ArrayList<LatAndLng>) t.getExtras().getSerializable("area");
        name = t.getStringExtra("name");
        workerName = t.getStringExtra("workerName");

        if (latAndLngArrayList != null) {
            while (i < latAndLngArrayList.size()) {
                LatLng latLng = new LatLng(latAndLngArrayList.get(i).getLat(), latAndLngArrayList.get(i).getLng());
                latLngArrayList.add(latLng);
                i++;
            }
            PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngArrayList)
                    .clickable(true);
            polygon = gMap.addPolygon(polygonOptions);
            polygon.setStrokeColor(Color.rgb(0, 0, 0));


        }
    }

    /**
     * this function is called when the location of the phone is changed.
     * the function creates a marker on the current location of the worker.
     * @param location the current location.
     */
    @Override
    public void onLocationChanged(Location location) {
        for (Marker marker : markerArrayList) marker.remove();
        if (markerArrayList != null) markerArrayList.clear();
        mlocation = location;
        LatLng latLng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        LatAndLng latAndLng = new LatAndLng(mlocation.getLatitude(),mlocation.getLongitude());
        if (toMove){
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            toMove = false;
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        Marker marker = gMap.addMarker(markerOptions);
        markerArrayList.add(marker);

        refDis.child(name).child("currentLocation").child(workerName).setValue(latAndLng);
        isCurrenLoc = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void back(View view) {
        locationManager.removeUpdates(this);
        if (isCurrenLoc)
            refDis.child(name).child("currentLocation").child(workerName).removeValue();
        finish();
    }
}
