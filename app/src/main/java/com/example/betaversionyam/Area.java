package com.example.betaversionyam;

import java.util.ArrayList;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		13/02/2020
 *
 * this java class allows to create a new Item named Area. Used to save the area of the distribution.
 */


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

