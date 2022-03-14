package com.bigyoshi.qrhunt;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

public class QRLocation {
    private double lat;
    private double lon;
    private String id;

    public QRLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        updateId();
    }

    public double getLat() { return lat; }

    public double getLong() { return lon; }

    public String getId() { return this.id; }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLong(double lon) {
        this.lon = lon;
    }

    public void updateId() {
        this.id = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.lat, this.lon));
    }
}
