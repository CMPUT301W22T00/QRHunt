package com.bigyoshi.qrhunt;

import android.location.Location;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.io.Serializable;

/**
 * Definition: Keeping track of location and geohash of a pair of latitude and longitude points
 * Note: NA
 * Issues: TBA
 */
public class QRLocation implements Serializable {
    private Double lat;
    private Double lon;
    private String geoHash;

    public QRLocation() {}
    /**
     * Constructor method
     *
     * @param lat latitude
     * @param lon longitude
     */
    public QRLocation(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
        computeGeoHash();
    }

    public QRLocation(Location location) {
        if (location != null) {
            this.lat = location.getLatitude();
            this.lon = location.getLongitude();
            computeGeoHash();
        }
    }

    private void computeGeoHash() {
        this.geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.lat, this.lon));
    }

    /**
     * Getter method
     *
     * @return latitude
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Getter method
     *
     * @return longitude
     */
    public Double getLong() {
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

    /*
     * Returns whether or not the location exists, ie, is a latitude and longitude specified
     */
    public boolean exists() {
        return getLat() != null && getLong() != null && getGeoHash() != null;
    }
}
