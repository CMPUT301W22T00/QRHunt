package com.bigyoshi.qrhunt;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;


public class AddQRCodeFragment extends DialogFragment {
    private String hash;
    private int value;
    //private QRLocation location;
    private double lat;
    private double lon;
    private TextView showHash;
    private TextView showValue;
    private TextView showLatLong;
    private TextView showNumScanned;
    private Button addImage;
    private FirebaseFirestore db;

    //public AddQRCodeFragment(String hash, int value, QRLocation location) {
    public AddQRCodeFragment(String hash, int value, double lat, double lon) {
        this.hash = hash;
        this.value = value;
        this.lat = lat;
        this.lon = lon;
        //this.location = location;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_qr_profile_after_scan, null);
        //showHash = view.findViewById((R.id.));
        showValue = view.findViewById(R.id.text_qr_value);
        showLatLong = view.findViewById(R.id.text_lon_lat);
        showNumScanned = view.findViewById(R.id.text_scans);

        //showHash.setText(hash);
        showValue.setText(String.valueOf(value));
        showLatLong.setText(String.valueOf(lat)+", "+String.valueOf(lon));
        showNumScanned.setText("01");

        db = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("ADD QR")
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ExternalQRCode qrCode = new ExternalQRCode(hash, value);
                        qrCode.setLocation(lat, lon);
                        qrCode.AddToDB(db);
                        qrCode.AddToQRLibrary(db);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })

                .create();
    }
}
