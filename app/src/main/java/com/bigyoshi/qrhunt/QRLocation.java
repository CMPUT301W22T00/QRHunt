package com.bigyoshi.qrhunt;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

/**
 * Definition: Keeping track of location and id of QR
 * Note: NA
 * Issues: TBA
 */
public class QRLocation {
    private double lat;
    private double lon;
    private String geoHash;

    public QRLocation() {}
    /**
     * Constructor method
     *
     * @param lat latitude
     * @param lon longitude
     */
    public QRLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        computeGeoHash();
    }

    private void computeGeoHash() {
        this.geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.lat, this.lon));
    }

    /**
     * Getter method
     *
     * @return latitude
     */
    public double getLat() {
        return lat;
    }

    /**
     * Getter method
     *
     * @return longitude
     */
    public double getLong() {
        return lon;
    }

    /**
     * Getter method
     *
     * @return QR id
     */
    public String getGeoHash() {
        return this.geoHash;
    }

    /**
     * Setter method
     *
     * @param lat latitude
     */
    public void setLat(double lat) {
        this.lat = lat;
        computeGeoHash();
    }

    /**
     * Setter method
     *
     * @param lon longitude
     */
    public void setLong(double lon) {
        this.lon = lon;
        computeGeoHash();
    }
}
