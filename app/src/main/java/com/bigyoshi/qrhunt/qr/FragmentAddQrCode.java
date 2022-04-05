package com.bigyoshi.qrhunt.qr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bigyoshi.qrhunt.R;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Definition: After scan fragment popup - displays values information and handles location photo
 * Note: NA
 * Issues: TBA
 */
public class FragmentAddQrCode extends DialogFragment {
    public static final String TAG = FragmentAddQrCode.class.getSimpleName();

    private TextView showScore;
    private TextView numScannedTextView;
    private Button addPicButton;
    private Bitmap bitmap;
    private ImageView imageView;
    private Boolean isHidden;
    private CodeScanner codeScanner;


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
        PlayableQrCode qrCode = (PlayableQrCode) getArguments().getSerializable(PlayableQrCode.TAG);
        // https://stackoverflow.com/questions/8045556/cant-make-the-custom-dialogfragment-transparent-over-the-fragment
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView = view.findViewById(R.id.qr_scan_profile_image_holder);
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
        ((TextView) view.findViewById(R.id.qr_scan_profile_score)).setText(String.format("%d points", qrCode.getScore()));

        numScannedTextView = view.findViewById(R.id.qr_scan_profile_num_scanned);
        ImageView uniqueFlag = view.findViewById(R.id.qr_after_scan_profile_unique_flag);
        db.collection("qrCodesMetadata").document(qrCode.getId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String numScanned = task.getResult().get("numScanned").toString();
                            numScannedTextView.setText("Scans: "+numScanned);
                            if (numScanned.matches("1")) {
                                uniqueFlag.setVisibility(View.VISIBLE);
                            }
                        } else {
                            numScannedTextView.setText("Scans: 0");
                        }
                    } else {
                        numScannedTextView.setText("Scans: 0");
                    }
                }
        );
        // Display location
        TextView showLatLong = view.findViewById(R.id.qr_scan_profile_location);
        QrLocation qrLocation = qrCode.getLocation();
        if (qrLocation != null && qrLocation.exists()) {
            String strLatitude = Location.convert(qrLocation.getLatitude(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(qrLocation.getLongitude(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("No Location");
        }

        ImageButton hideLocation = view.findViewById(R.id.qr_scan_profile_toggle_visibility);
        isHidden = false;
        hideLocation.setOnClickListener( view1 -> {
            if (qrLocation != null && qrLocation.exists()) {
                if (isHidden) {
                    qrCode.setLocation(qrLocation);
                    String strLatitude = Location.convert(qrLocation.getLatitude(), Location.FORMAT_DEGREES);
                    String strLongitude = Location.convert(qrLocation.getLongitude(), Location.FORMAT_DEGREES);
                    showLatLong.setText(strLatitude + ", " + strLongitude);
                    hideLocation.setBackgroundResource(R.drawable.ic_button_location_on);
                    isHidden = false;
                } else {
                    qrCode.setLocation(null);
                    showLatLong.setText("No Location");
                    hideLocation.setBackgroundResource(R.drawable.ic_button_location_off);
                    isHidden = true;
                }
            }
        });

        // Take A picture
        addPicButton = view.findViewById(R.id.qr_scan_profile_take_photo_button);
        addPicButton.setOnClickListener(__ -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pickPhotoResultLauncher.launch(intent);
            addPicButton.setVisibility(View.GONE);
            addPicButton.setClickable(false);
        });

        // Goes back to the scanner without saving anything
        Button cancelButton = view.findViewById(R.id.qr_scan_profile_cancel_button);
        cancelButton.setOnClickListener(__ -> {
            dismiss();
            FragmentScanner.codeScanner.setScanMode(ScanMode.SINGLE);
                });


        // Adds QR to Account
        Button okButton = view.findViewById(R.id.qr_scan_profile_save_button);
        Query qrList =  db.collection("users").document(qrCode.getPlayerId()).collection("qrCodes");
        qrList.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (doc.exists()) {
                        PlayableQrCode qrCodeDB = doc.toObject(PlayableQrCode.class);
                        if (qrCodeDB.getId().matches(qrCode.getId())) {
                            cancelButton.setText("Already");
                            cancelButton.setClickable(false);
                            okButton.setText("Scanned");
                            okButton.setClickable(false);
                            ImageView scannedFlag = view.findViewById(R.id.qr_after_scan_profile_scanned_flag);
                            scannedFlag.setVisibility(View.VISIBLE);
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dismiss();
                                    FragmentScanner.codeScanner.setScanMode(ScanMode.SINGLE);
                                }
                            }, 3000);
                        }
                    }
                }
            }
        });
        okButton.setOnClickListener(__ -> {
            Toast toast = Toast.makeText(getContext(), "QR ADD", Toast.LENGTH_LONG);
            LinearLayout overlay = view.findViewById(R.id.qr_scan_profile_fader_layout);
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
                                qrCode.addToDb();
                                overlay.setVisibility(View.INVISIBLE);
                                dismiss();
                                FragmentScanner.codeScanner.startPreview();
                            }
                        }));
            }
            qrCode.addToDb();
            overlay.setVisibility(View.INVISIBLE);
            dismiss();
            FragmentScanner.codeScanner.setScanMode(ScanMode.PREVIEW);
        });

        return view;
    }

    /**
     * Creates new instance for QR to add
     *
     * @param playableQRCode QR to add
     * @return QR to add
     */
    public static FragmentAddQrCode newInstance(PlayableQrCode playableQRCode) {
        FragmentAddQrCode fragmentAddQRCode = new FragmentAddQrCode();
        Bundle args = new Bundle();
        args.putSerializable(PlayableQrCode.TAG, playableQRCode);
        fragmentAddQRCode.setArguments(args);

        return fragmentAddQRCode;
    }

}
