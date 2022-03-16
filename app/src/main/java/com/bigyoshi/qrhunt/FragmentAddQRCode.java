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
 * Note: NA
 * Issues: TBA
 */
public class FragmentAddQRCode extends DialogFragment {
    public static final String TAG = FragmentAddQRCode.class.getSimpleName();

    private TextView showScore;
    private TextView numScannedTextView;
    private Button addPicButton;
    private Bitmap bitmap;
    private ImageView imageView;

    public FragmentAddQRCode() {}

    /**
     * After scanning QR code - Handles the displaying and saving of the QR code values (score, number of scans, location)
     * and is responsible for attaching the user's photo in the proper position
     *
     * @param inflater           Inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_profile_after_scan, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        PlayableQRCode qrCode = (PlayableQRCode) getArguments().getSerializable(PlayableQRCode.TAG);

        ImageView imageView = view.findViewById(R.id.image_holder);
        ActivityResultLauncher<Intent> pickPhotoResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        bitmap = (Bitmap) result.getData().getExtras().get("data");
                        Log.d(TAG, String.format("found image with %d bytes", bitmap.getRowBytes() * bitmap.getHeight()));
                        imageView.setImageBitmap(bitmap);
                    }
                });

        // Display score
        ((TextView) view.findViewById(R.id.text_qr_score)).setText(String.format("%d points", qrCode.getScore()));

        numScannedTextView = view.findViewById(R.id.text_scans);
        numScannedTextView.setText("0 Scans");
        db.collection("users").document(qrCode.getPlayerId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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
                }
        );
        // Display location
        TextView showLatLong = view.findViewById(R.id.text_lon_lat);
        QRLocation qrLocation = qrCode.getLocation();
        if (qrLocation.exists()) {
            String strLatitude = Location.convert(qrLocation.getLatitude(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(qrLocation.getLongitude(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("LOCATION NOT GIVEN");
        }

        addPicButton = view.findViewById(R.id.button_take_photo);
        addPicButton.setOnClickListener(__ -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pickPhotoResultLauncher.launch(intent);
            addPicButton.setVisibility(View.GONE);
            addPicButton.setClickable(false);
        });


        Button okButton = view.findViewById(R.id.qr_button_ok);
        okButton.setOnClickListener(__ -> {
            LinearLayout overlay = view.findViewById(R.id.fader_layout);
            overlay.setVisibility(View.VISIBLE);

            if (bitmap != null) {
                StorageReference ref = storage.getReference(String.format("qrCodes/%s/%s.jpg", qrCode.getPlayerId(), qrCode.getId()));
                ByteArrayOutputStream compressedBitmap = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, compressedBitmap);
                UploadTask uploadTask = ref.putBytes(compressedBitmap.toByteArray());
                uploadTask.addOnFailureListener(exception -> Log.d(TAG, "Image upload failed: " + exception))
                        .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(uriTask -> {
                            if (uriTask.isSuccessful() && uriTask.getResult() != null) {
                                qrCode.setImageUrl(uriTask.getResult().toString());
                                Log.d(TAG, "Image upload succeeded to " + uriTask.getResult().toString());
                            }
                            qrCode.addToDb();
                            overlay.setVisibility(View.INVISIBLE);
                            dismiss();
                        }));
            } else {
                qrCode.addToDb();
                overlay.setVisibility(View.INVISIBLE);
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.qr_button_cancel);
        cancelButton.setOnClickListener(__ -> dismiss());

        return view;
    }

    /**
     * Creates new instance for QR to add
     *
     * @param playableQRCode QR to add
     * @return QR to add
     */
    public static FragmentAddQRCode newInstance(PlayableQRCode playableQRCode) {
        FragmentAddQRCode fragmentAddQRCode = new FragmentAddQRCode();
        Bundle args = new Bundle();
        args.putSerializable(PlayableQRCode.TAG, playableQRCode);
        fragmentAddQRCode.setArguments(args);

        return fragmentAddQRCode;
    }

}
