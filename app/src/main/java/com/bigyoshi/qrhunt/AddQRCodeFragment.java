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
    private QRLocation location;
    private TextView showHash;
    private TextView showValue;
    private TextView showLat;
    private TextView showLong;
    private Button addImage;
    private FirebaseFirestore db;

    public AddQRCodeFragment(String hash, int value, QRLocation location) {
        this.hash = hash;
        this.value = value;
        this.location = location;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_qr_fragment, null);
        showHash = view.findViewById((R.id.show_hash));
        showValue = view.findViewById(R.id.show_score);
        showLat = view.findViewById(R.id.show_latitude);
        showLong = view.findViewById(R.id.show_longitude);

        showHash.setText(hash);
        showValue.setText(String.valueOf(value));
        showLat.setText(String.valueOf(location.getLat()));
        showLong.setText(String.valueOf(location.getLong()));

        db = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("ADD QR")
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ExternalQRCode qrCode = new ExternalQRCode(hash, value);
                        qrCode.setLocation(location.getLat(), location.getLong());
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
