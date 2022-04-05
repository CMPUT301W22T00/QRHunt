package com.bigyoshi.qrhunt.bottom_navigation.map;

import android.widget.TextView;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.qr.PlayableQrCode;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.DecimalFormat;

/**
 * Definition: A custom information window for the markers/pins
 * Note: N/A
 * Issues: N/A
 */
public class CustomInfoWindow extends InfoWindow {

        String distance;
        PlayableQrCode qr;

    /**
     * Constructor method
     *
     * @param mapView   map view
     * @param distance  distance from player location
     * @param qr        qr code
     */
    public CustomInfoWindow(MapView mapView, String distance, PlayableQrCode qr) {
            super(R.layout.map_qr_info_callout, mapView);
            this.distance = distance;
            this.qr = qr;
        }

        public void onClose() {

        }

    /**
     * Called when the call out is opened (presents call out)
     *
     * @param arg0  object
     */
    @Override
        public void onOpen(Object arg0) {
            TextView txtDistance = (TextView) mView.findViewById(R.id.map_call_out_distance);
            TextView txtScore = (TextView) mView.findViewById(R.id.map_call_out_score);
            TextView txtScans = (TextView) mView.findViewById(R.id.map_call_out_num_scans);
            TextView txtLocation = (TextView) mView.findViewById(R.id.map_call_out_location);

            // Just some formatting
            Double latitude = qr.getLocation().getLatitude();
            Double longitude = qr.getLocation().getLongitude();
            DecimalFormat value = new DecimalFormat("#.#");
            String lat = value.format(latitude);
            String lng = value.format(longitude);

            // Creating strings to set the TextView
            String dist = "Distance: " + distance;
            String points = "Value: " + String.valueOf(qr.getScore());
            String numScan = "Scans: "+ String.valueOf(qr.getNumScanned());
            String loc = "Lat: " + lat + ", Long: " + lng;

            // Settings the text
            txtDistance.setText(dist);
            txtScore.setText(points);
            txtScans.setText(numScan);
            txtLocation.setText(loc);
        }
}
