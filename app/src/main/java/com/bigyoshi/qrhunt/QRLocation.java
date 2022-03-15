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
    private String id;

    /**
     * Constructor method
     * @param lat latitude
     * @param lon longitude
     */
    public QRLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        updateId();
    }

    /**
     * Getter method
     * @return latitude
     */
    public double getLat() {
        return lat;
    }

    /**
     * Getter method
     * @return longitude
     */
    public double getLong() {
        return lon;
    }

    /**
     * Getter method
     * @return QR id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter method
     * @param lat latitude
     */
    public void setLat(double lat) {

        this.lat = lat;
    }

    /**
     * Setter method
     * @param lon longitude
     */
    public void setLong(double lon) {

        this.lon = lon;
    }

    /**
     * Updates the QR location
     */
    public void updateId() {
        this.id = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.lat, this.lon));
    }
}
