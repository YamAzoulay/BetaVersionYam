package com.example.betaversionyam;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    Intent gi = new Intent();
    GoogleMap gMap;
    Polygon polygon;
    ArrayList<LatLng> latLngList = new ArrayList<>();
    ArrayList<Marker> markerList = new ArrayList<>();
    ArrayList<LatAndLng> latAndLngArrayList = new ArrayList<>();
    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        checkBox = findViewById(R.id.checkBox);
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MapActivity.this);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (polygon == null) return;
                    polygon.setFillColor(Color.rgb(0,0,0));
                }
                else {
                    polygon.setFillColor(Color.TRANSPARENT);
                }
            }
        });
    }


    @Override
    public void onMapReady (GoogleMap googleMap){
        gMap = googleMap;
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                Marker marker = gMap.addMarker(markerOptions);
                latLngList.add(latLng);
                markerList.add(marker);
                LatAndLng latAndLng = new LatAndLng(latLng.latitude , latLng.longitude);
                latAndLngArrayList.add(latAndLng);
            }
        });

    }

    public void drawPolygon(View view) {
        if (polygon!=null) polygon.remove();
        ArrayList<LatLng> copy = (ArrayList) latLngList.clone();
        PolygonOptions polygonOptions = new PolygonOptions().addAll(copy)
                .clickable(true);
        polygon = gMap.addPolygon(polygonOptions);
        polygon.setStrokeColor(Color.rgb(0,0,0));
        if (checkBox.isChecked())
            polygon.setFillColor(Color.rgb(0,0,0));
    }

    public void clear(View view) {
        if (polygon!=null) polygon.remove();
            for (Marker marker : markerList) marker.remove();
            latLngList.clear();
            markerList.clear();
            latAndLngArrayList.clear();
            checkBox.setChecked(false);
    }

    public void Done(View view) {
        if (latAndLngArrayList!=null) {
            gi.putExtra("selectedArea", latAndLngArrayList);
            setResult(RESULT_OK, gi);
            finish();
        }
    }
}
