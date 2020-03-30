package com.example.betaversionyam;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;

public class Area {
    private ArrayList<LatAndLng> latAndLngArrayList;

    public Area(ArrayList<LatAndLng> latAndLngArrayList) {
        this.latAndLngArrayList = latAndLngArrayList;
    }

    public Area(){
    }

    public ArrayList<LatAndLng> getLatAndLngArrayList() {
        return latAndLngArrayList;
    }

    public void setLatAndLngArrayList(ArrayList<LatAndLng> latAndLngArrayList) {
        this.latAndLngArrayList = latAndLngArrayList;
    }
}

