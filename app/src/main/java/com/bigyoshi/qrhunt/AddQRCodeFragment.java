package com.bigyoshi.qrhunt;

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
    private TextView title;
    private TextView showValue;
    private TextView showLat;
    private TextView showLong;
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

        title = view.findViewById(R.id.text_qr_value);
        title.setText(String.valueOf(this.score));
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
