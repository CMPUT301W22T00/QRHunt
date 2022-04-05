package com.bigyoshi.qrhunt.bottom_navigation.map;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.qr.PlayableQrCode;
import com.bigyoshi.qrhunt.qr.QrLocation;
import com.google.zxing.qrcode.encoder.QRCode;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.DecimalFormat;

/**
 * Definition: A custom information window for the markers/pins
 * Note: N/A
 * Issues: The pins don't close when you tap on a different one
 */
public class CustomInfoWindow extends InfoWindow {

    String distance;
    PlayableQrCode qr;
    String playerId;

    public CustomInfoWindow(MapView mapView, String distance, PlayableQrCode qr, String playerId) {
        super(R.layout.map_qr_info_callout, mapView);
        this.distance = distance;
        this.qr = qr;
    }

    public void onClose() {

    }

    @Override
    public void onOpen(Object arg0) {
        TextView txtDistance = (TextView) mView.findViewById(R.id.map_call_out_distance);
        TextView txtScore = (TextView) mView.findViewById(R.id.map_call_out_score);
        TextView txtScans = (TextView) mView.findViewById(R.id.map_call_out_num_scans);
        TextView txtLocation = (TextView) mView.findViewById(R.id.map_call_out_location);
        ImageView scannedFlag = (ImageView) mView.findViewById(R.id.map_call_out_scanned_flag);

        // Just some formatting
        Double latitude = qr.getLocation().getLatitude();
        Double longitude = qr.getLocation().getLongitude();
        DecimalFormat value = new DecimalFormat("#.#");
        String lat = value.format(latitude);
        String lng = value.format(longitude);

        // Creating strings to set the TextView
        String dist = "Distance: " + distance;
        String points = "Value: " + String.valueOf(qr.getScore());
        String numScan = "Scans: "+ qr.getNumScanned();
        String loc = "Lat: " + lat + ", Lon: " + lng;
        if (playerId.matches(qr.getPlayerId())) { scannedFlag.setVisibility(View.VISIBLE); }

        // Settings the text
        txtDistance.setText(dist);
        txtScore.setText(points);
        txtScans.setText(numScan);
        txtLocation.setText(loc);
    }
}
