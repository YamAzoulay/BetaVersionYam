package com.example.betaversionyam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class WorkerMapActivity extends AppCompatActivity implements OnMapReadyCallback , LocationListener {
    LocationManager locationManager;
    Location mlocation;
    Intent t;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap gMap;
    ArrayList<LatAndLng> latAndLngArrayList;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    Polygon polygon;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_map);
        t=getIntent();
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.workerMap);
        supportMapFragment.getMapAsync(WorkerMapActivity.this);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    mlocation=location;
                }
            }
        });
    }

    public void drawPolygon(View view) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;

        latAndLngArrayList = (ArrayList<LatAndLng>) t.getExtras().getSerializable("area");

        if (latAndLngArrayList.size() != 0) Toast.makeText(this, latAndLngArrayList.size() + "", Toast.LENGTH_SHORT).show();
        while (i < latAndLngArrayList.size()){
            LatLng latLng = new LatLng(latAndLngArrayList.get(i).getLat() , latAndLngArrayList.get(i).getLng());
            latLngArrayList.add(latLng);
            i++;
        }

        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngArrayList)
                .clickable(true);
        if (latLngArrayList.isEmpty()) Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        else{ polygon = gMap.addPolygon(polygonOptions);
        polygon.setStrokeColor(Color.rgb(0,0,0));}
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        gMap.addMarker(markerOptions);
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
        finish();
    }
}
