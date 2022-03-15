package com.bigyoshi.qrhunt;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

/**
 * Definition: class for keeping track of location and id of QR
 *
 *
 */
public class QRLocation {
    private double lat;
    private double lon;
    private String id;

    /**
     * constructor: assigns lat and lon and updates id
     * @param lat
     * @param lon
     */
    public QRLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        updateId();
    }

    /**
     *
     * @return
     */
    public double getLat() { return lat; }

    /**
     *
     * @return
     */
    public double getLong() { return lon; }

    /**
     *
     * @return
     */
    public String getId() { return this.id; }

    /**
     *
     * @param lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     *
     * @param lon
     */
    public void setLong(double lon) {
        this.lon = lon;
    }

    /**
     *
     */
    public void updateId() {
        this.id = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.lat, this.lon));
    }
}
