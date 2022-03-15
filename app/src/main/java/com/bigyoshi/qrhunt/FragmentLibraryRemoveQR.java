package com.bigyoshi.qrhunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FragmentLibraryRemoveQR extends DialogFragment {

    private int pos;
    private PlayableQRCode removeQR;
    private TextView showScore;
    private TextView showLatLong;
    private TextView showNumScanned;
    private ImageView showPic;
    private Button okayButton;
    private Button cancelButton;
    private Button addPic;
    private Bitmap bitmap;

    public FragmentLibraryRemoveQR(int i, PlayableQRCode removeQR) {
        this.pos = pos;
        this.removeQR = removeQR;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_profile_after_scan, container, false);

        // Display score
        showScore = view.findViewById(R.id.text_qr_score);
        showScore.setText(String.valueOf(removeQR.getScore()));

        // Display numScan
        showNumScanned = view.findViewById(R.id.text_scans);
        showNumScanned.setText("01"); // HARD CODED FOR NOW

        // Display location
        showLatLong = view.findViewById(R.id.text_lon_lat);
        if (removeQR.getLocation() != null) {
            String strLatitude = Location.convert(removeQR.getLocation().getLat(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(removeQR.getLocation().getLong(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("LOCATION NOT GIVEN");
        }

        // attach photo
        showPic = view.findViewById(R.id.image_holder);
        addPic = view.findViewById(R.id.button_take_photo);
        addPic.setVisibility(View.GONE);

        okayButton = view.findViewById(R.id.button_ok);
        okayButton.setText("REMOVE");
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentProfile parentFrag = ((FragmentProfile)FragmentLibraryRemoveQR.this.getParentFragment());
                parentFrag.libraryRemoveQR(pos, removeQR);
                getFragmentManager().beginTransaction().remove(FragmentLibraryRemoveQR.this).commit();
            }
        });

        cancelButton = view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(FragmentLibraryRemoveQR.this).commit();
            }
        });

        return view;
    }
}
