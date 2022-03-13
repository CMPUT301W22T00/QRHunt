package com.bigyoshi.qrhunt;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;


public class AddQRCodeFragment extends DialogFragment {
    private String hash;
    private int score;
    private QRLocation location;
    private TextView showScore;
    private TextView showLatLong;
    private TextView showNumScanned;
    private Button addImage;
    private FirebaseFirestore db;

    public AddQRCodeFragment(String hash, int score, QRLocation location) {
        this.hash = hash;
        this.score = score;
        this.location = location;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_profile_after_scan, container, false);

        showScore = view.findViewById(R.id.text_qr_score);
        showLatLong = view.findViewById(R.id.text_lon_lat);
        showNumScanned = view.findViewById(R.id.text_scans);

        String strLatitude = Location.convert(location.getLat(), Location.FORMAT_DEGREES);
        String strLongitude = Location.convert(location.getLong(), Location.FORMAT_DEGREES);

        showScore.setText(String.valueOf(score));
        showLatLong.setText(strLatitude+", "+strLongitude);
        showNumScanned.setText("01");

        // todo: all other stuff
        // caption
        // attach photo
        // disable location (toggle not present in UI right now but should be probably?)
        // probably skip num scanned for now, it's obnoxious
        // need to have a cancel button as well
        // after ok button → save to db
        return view;
    }


}
