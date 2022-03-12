package com.bigyoshi.qrhunt;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

public class Location {
    private double lat;
    private double lon;
    private String id;

    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        updateId();
    }

    public double getLat() { return lat; }

    public void setLat(double lat) {
        this.lat = lat;
        updateId();
    }

    public double getLon() { return lon; }

    public void setLon(double lon) {
        this.lon = lon;
        updateId();
    }

    public String getId() { return this.id; }

    public void updateId() {
        this.id = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.lat, this.lon));
    }
}
