package com.bigyoshi.qrhunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Definition: After scan fragment popup - displays values information and handles location photo
 * Note: NA
 * Issues: TBA
 */
public class FragmentAddQRCode extends DialogFragment {
    private String hash;
    private String playerId;
    private int score;
    private QRLocation location;
    private PlayableQRCode qrCode;
    private TextView showScore;
    private TextView showLatLong;
    private TextView showNumScanned;
    private ImageView showPic;
    private Button okayButton;
    private Button addPic;
    private Bitmap bitmap;
    private FirebaseFirestore db;

    /**
     * Constructor method
     * @param hash QR hash
     * @param score QR score
     * @param location QR location
     * @param playerId Current player id
     */
    public FragmentAddQRCode(String hash, int score, QRLocation location, String playerId) {
        this.hash = hash;
        this.score = score;
        this.location = location;
        this.playerId = playerId;
    }

    /**
     *  After scanning QR code - Handles the displaying and saving of the QR code values (score, number of scans, location)
     *  and is responsible for attaching the user's photo in the proper position
     * @param inflater Inflater
     * @param container Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_profile_after_scan, container, false);

        // Display score
        showScore = view.findViewById(R.id.text_qr_score);
        showScore.setText(String.valueOf(score));

        // Display numScan
        showNumScanned = view.findViewById(R.id.text_scans);
        showNumScanned.setText("01"); // HARD CODED FOR NOW

        // Display location
        showLatLong = view.findViewById(R.id.text_lon_lat);
        if (location != null) {
            String strLatitude = Location.convert(location.getLat(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(location.getLong(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("LOCATION NOT GIVEN");
        }

        // attach photo
        showPic = view.findViewById(R.id.image_holder);
        addPic = view.findViewById(R.id.button_take_photo);
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
                addPic.setVisibility(View.GONE);
                addPic.setClickable(false);
                }
            });

        // todo: all other stuff
        // caption
        // disable location (toggle not present in UI right now but should be probably?)   I wll have this added in for project 4 - Alinn
        // probably skip num scanned for now, it's obnoxious
        // need to have a cancel button as well
        // after ok button → save to db
        okayButton = view.findViewById(R.id.button_ok);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();
                qrCode = new PlayableQRCode(hash, score);
                if (location != null) {
                    qrCode.setLocation(location.getLat(), location.getLong());
                }
                if (bitmap != null) {
                    qrCode.setImage(bitmap);
                }
                if (playerId != null) {
                    qrCode.AddToQRLibrary(db, playerId);
                } else {
                    qrCode.AddToQRLibrary(db, "c6670e44-1fe2-4b98-acfd-98c55767cf3c"); // Hard coded userId
                }
                getFragmentManager().beginTransaction().remove(FragmentAddQRCode.this).commit();
            }
        });
        return view;
    }

    /**
     * Displays player's photo
     * @param requestCode request code
     * @param resultCode result code
     * @param data data associated with player's photo
     * @deprecated onActivityResult
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            bitmap = (Bitmap) data.getExtras().get("data");
            showPic.setImageBitmap(bitmap);
        }
    }



}
