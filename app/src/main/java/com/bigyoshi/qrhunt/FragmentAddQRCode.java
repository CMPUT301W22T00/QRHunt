package com.bigyoshi.qrhunt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Definition: After scan fragment popup - displays values information and handles location photo
 */
public class FragmentAddQRCode extends DialogFragment {
    private static final String TAG = FragmentAddQRCode.class.getSimpleName();
    private String hash;
    private String playerId;
    private int score;
    private QRLocation location;
    private PlayableQRCode qrCode;
    private TextView showScore;
    private TextView showLatLong;
    private TextView numScannedTextView;
    private Button addPicButton;
    private Bitmap bitmap;
    private FirebaseFirestore db;
    private ImageView imageView;
    private FirebaseStorage storage;

    /**
     * Constructor
     *
     * @param hash
     * @param score
     * @param location
     * @param playerId
     */
    public FragmentAddQRCode(String hash, int score, QRLocation location, String playerId) {
        this.hash = hash;
        this.score = score;
        this.location = location;
        this.playerId = playerId;

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * After scanning QR code - Handles the displaying and saving of the QR code values (score, number of scans, location)
     * and is responsible for attaching the user's photo in the proper position
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_profile_after_scan, container, false);
        ImageView imageView = view.findViewById(R.id.image_holder);

        ActivityResultLauncher<Intent> pickPhotoResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            bitmap = (Bitmap) result.getData().getExtras().get("data");
                            Log.d(TAG, String.format("found image with %d bytes", bitmap.getRowBytes() * bitmap.getHeight()));
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });

        // Display score
        ((TextView) view.findViewById(R.id.text_qr_score)).setText(String.format("%d points", score));

        numScannedTextView = view.findViewById(R.id.text_scans);
        numScannedTextView.setText("0 Scans");
        db.collection("users").document(playerId).get().addOnCompleteListener(
                task -> {
                    DocumentSnapshot res = task.getResult();
                    if (res != null && res.exists()) {
                        Double numScanned = res.getDouble("numScanned");
                        int intNumScanned;
                        if (numScanned != null) {
                            intNumScanned = numScanned.intValue();
                        } else {
                            intNumScanned = 0;
                        }
                        numScannedTextView.setText(String.format("%d Scans", intNumScanned));
                    }
                }
        );
        // Display location
        showLatLong = view.findViewById(R.id.text_lon_lat);
        if (location != null) {
            String strLatitude = Location.convert(location.getLat(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(location.getLong(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("LOCATION NOT GIVEN");
        }

        // Get a photo to attach
        //        imageView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //                imageView.setVisibility(View.GONE);
        //                imageView.setClickable(false);
        //            }
        //        });

        addPicButton = view.findViewById(R.id.button_take_photo);
        addPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pickPhotoResultLauncher.launch(intent);
                addPicButton.setVisibility(View.GONE);
                addPicButton.setClickable(false);
            }
        });


        Button okButton = view.findViewById(R.id.qr_button_ok);
        okButton.setOnClickListener(__ -> {
            qrCode = new PlayableQRCode(hash, score);
            if (location != null) {
                qrCode.setLocation(location.getLat(), location.getLong());
            }
            LinearLayout overlay = view.findViewById(R.id.fader_layout);
            overlay.setVisibility(View.VISIBLE);

            if (bitmap != null) {
                StorageReference ref = storage.getReference(String.format("qrCodes/%s/%s.jpg", playerId, hash));
                ByteArrayOutputStream compressedBitmap = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, compressedBitmap);
                UploadTask uploadTask = ref.putBytes(compressedBitmap.toByteArray());
                uploadTask.addOnFailureListener(exception -> {
                    Log.d(TAG, "Image upload failed: " + exception.toString());
                }).addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnCompleteListener(uriTask -> {
                        if (uriTask.isSuccessful() && uriTask.getResult() != null) {
                            qrCode.setImage(uriTask.getResult().toString());
                            Log.d(TAG, "Image upload succeeded to " + uriTask.getResult().toString());
                        }
                        qrCode.AddToQRLibrary(db, playerId);
                        overlay.setVisibility(View.INVISIBLE);
                        dismiss();
                    });
                });
            } else {
                qrCode.AddToQRLibrary(db, playerId);
                overlay.setVisibility(View.INVISIBLE);
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.qr_button_cancel);
        cancelButton.setOnClickListener(__ -> dismiss());

        return view;
    }

}
