package com.example.betaversionyam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		26/02/2020
 *
 * In this activity the manager choose the area of the distribution.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    Intent gi = new Intent();
    GoogleMap gMap;
    Polygon polygon;
    ArrayList<LatLng> latLngList = new ArrayList<>();
    ArrayList<Marker> markerList = new ArrayList<>();
    ArrayList<LatAndLng> latAndLngArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MapActivity.this);

    }


    /**
     * this function is called when the map is ready.
     * the function creates a marker in the location the user clicked on
     * and stores the lat and lng of the location in array list.
     * @param googleMap the displayed map.
     */

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

    /**
     * this function is called when the manager clicks on the button "draw a polygon"
     * the function creates a polygon from the locations of the markers that the user create.
     */

    public void drawPolygon(View view) {
        if (polygon!=null) polygon.remove();
        if (!latLngList.isEmpty()) {
            ArrayList copy = (ArrayList) latLngList.clone();
            PolygonOptions polygonOptions = new PolygonOptions().addAll(copy)
                    .clickable(true);
            polygon = gMap.addPolygon(polygonOptions);
            polygon.setStrokeColor(Color.rgb(0, 0, 0));
        }
        else
            Toast.makeText(this, "you must select locations first.", Toast.LENGTH_SHORT).show();
    }

    /**
     * this function is called when the manager clicks on the button "Clear"
     * the function clears all the lists, remove the polygon and the markers.
     */

    public void clear(View view) {
        if (polygon!=null) polygon.remove();
        for (Marker marker : markerList) marker.remove();
        latLngList.clear();
        markerList.clear();
        latAndLngArrayList.clear();
    }

    /**
     * this function is called when the manager clicks on the button "done"
     * the function sent the user to the last activity (newDistribution) with the array list of the locations
     * that create the polygon.
     */
    public void Done(View view) {
        if (latAndLngArrayList!=null) {
            gi.putExtra("selectedArea", latAndLngArrayList);
            setResult(RESULT_OK, gi);
            finish();
        }
    }

}
