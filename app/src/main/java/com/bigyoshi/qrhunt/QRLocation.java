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
    private Double latitude;
    private Double longitude;
    private String geoHash;

    /**
     * Constructor method
     *
     */
    public QRLocation() {}

    /**
     * Constructor method
     *
     * @param latitude latitude
     * @param lon      longitude
     */
    public QRLocation(Double latitude, Double lon) {
        this.latitude = latitude;
        this.longitude = lon;
        computeGeoHash();
    }

    /**
     * Constructor method
     *
     * @param location location
     */
    public QRLocation(Location location) {
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            computeGeoHash();
        }
    }

    /**
     * Tries to set geoHash if it can. In cases where the object is deserialized from the database,
     * latitude and longitude are set one by one, so we can't be sure if we have one we have both
     *
     */
    private void computeGeoHash() {
        if (getLongitude() != null && getLatitude() != null) {
            this.geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(getLatitude(), getLongitude()));
        }
    }

    /**
     * Getter method
     *
     * @return latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Getter method
     *
     * @return longitude
     */
    public Double getLongitude() {
        return longitude;
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
     * @param latitude latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
        computeGeoHash();
    }

    /**
     * Setter method
     *
     * @param longitude longitude
     */
    public void setLong(double longitude) {
        this.longitude = longitude;
        computeGeoHash();
    }

    /**
     * Returns whether or not the location exists, ie, is a latitude and longitude specified
     *
     * @return Boolean indicating whether the location exists or nto
     */
    public boolean exists() {
        return getLatitude() != null && getLongitude() != null && getGeoHash() != null;
    }
}
