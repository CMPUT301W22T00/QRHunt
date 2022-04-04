package com.bigyoshi.qrhunt.qr;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.player.Player;
import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.ScanMode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Definition: Scanner with camera - Scans and decodes QR code
 * Note: NA
 * Issues: TBA
 */
public class FragmentScanner extends Fragment {
    public static final String TAG = FragmentScanner.class.getSimpleName();
    public static CodeScanner codeScanner;
    private QrCodeProcessor camera;
    private String playerId;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     *
     * @param inflater           Inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return root
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getActivity().getSupportFragmentManager().setFragmentResultListener("getPlayer",
                this,
                (requestKey, result) -> {
                    Player player = (Player) result.getSerializable("player");
                    playerId = player.getPlayerId();
                });

        //Get permissions first
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        });

        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scanner, container, false);

        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        assert activity != null;
        codeScanner = new CodeScanner(activity, scannerView);

        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setScanMode(ScanMode.SINGLE);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setFlashEnabled(false);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);

        codeScanner.setDecodeCallback(result -> activity.runOnUiThread(() -> {
            camera = new QrCodeProcessor(FragmentScanner.this, result.getText(), playerId);
            camera.processQRCode();
            codeScanner.setScanMode(ScanMode.PREVIEW);
            codeScanner.startPreview();
        }));

        codeScanner.setErrorCallback(thrown -> Log.e(TAG, "Camera has failed: ", thrown ));
        return root;
    }

    /**
     * Handles when the state is resumed (starts camera previous)
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    /**
     * Handles when the state is paused (release resources)
     *
     */
    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    /**
     * Provides permissions (consent from the user)
     *
     * @param requestCode  request code
     * @param permissions  permissions
     * @param grantResults grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays
                .asList(permissions)
                .subList(0, grantResults.length));

        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Requests permissions
     *
     * @param permissions list of strings for permissions
     */
    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}