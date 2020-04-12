package com.example.betaversionyam;

import java.io.Serializable;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		13/02/2020
 *
 * this java class allows to create a new Item named LatAndLng. Used to save the latitude and longitude of a location
 *
 */
public class LatAndLng implements Serializable {
    private Double lat;
    private Double lng;

    public LatAndLng(){ }
    public LatAndLng(Double lat, Double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
