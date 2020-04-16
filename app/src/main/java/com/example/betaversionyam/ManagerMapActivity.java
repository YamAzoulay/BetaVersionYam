package com.example.betaversionyam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.betaversionyam.FBref.refDis;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		28/03/2020
 *
 * this screen displays the area of the distribution and the current location of the workers.
 */


public class ManagerMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    ValueEventListener disListener;
    Intent back;
    String name;
    LatAndLng latAndLng;
    GoogleMap gMap;
    Polygon polygon;
    int i = 0;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    Area area;
    ArrayList<Marker> markerArrayList = new ArrayList<>();
    LatLng temp;
    boolean isCameraAnimated  = false;

    /**
     * the function makes a connection between the variables in the java to the xml components,
     *  asks for permissions and initialize the map.
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_map);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            Toast.makeText(this, "you must allow the permission", Toast.LENGTH_SHORT).show();
            return;
        }

       else{
            SupportMapFragment supportMapFragment = (SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.managerMap);
            supportMapFragment.getMapAsync(ManagerMapActivity.this);
        }

        back=getIntent();
        if (back!=null) name=back.getStringExtra("name");

    }



    /**
     * this function is called when the map is ready.
     * the function creates the same polygon as the manager created and shows the current location of all the workers.
     * @param googleMap the displayed map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        refDis.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals("area")) {
                        area = data.getValue(Area.class);
                        ArrayList<LatAndLng> latAndLngArrayList = area.getLatAndLngArrayList();
                        if (latAndLngArrayList != null) {
                            while (i < latAndLngArrayList.size()) {
                                LatLng latLng = new LatLng(latAndLngArrayList.get(i).getLat(), latAndLngArrayList.get(i).getLng());
                                latLngArrayList.add(latLng);
                                i++;
                            }
                            temp = latLngArrayList.get(0);
                            PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngArrayList)
                                    .clickable(true);
                            polygon = gMap.addPolygon(polygonOptions);
                            polygon.setStrokeColor(Color.rgb(0, 0, 0));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        disListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                        latAndLng = data.getValue(LatAndLng.class);
                        if (latAndLng != null) {
                            for (Marker marker : markerArrayList){
                                if (marker.getTitle().equals(data.getKey())){
                                    marker.remove();
                                    markerArrayList.remove(marker);
                                }
                            }
                            LatLng latLng = new LatLng(latAndLng.getLat(), latAndLng.getLng());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                            markerOptions.title(data.getKey());
                            Marker marker = gMap.addMarker(markerOptions);
                            markerArrayList.add(marker);
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                            isCameraAnimated = true;
                        }
                    }
                }


                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }
            };
        refDis.child(name).child("currentLocation").addValueEventListener(disListener);

        if (!isCameraAnimated) gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp,16));

    }
}
