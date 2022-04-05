package com.bigyoshi.qrhunt.qr;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Definition: Scanner with camera - Scans and decodes QR code
 * Note: NA
 * Issues: TBA
 */
public class FragmentScanner extends Fragment {
    public static final String TAG = FragmentScanner.class.getSimpleName();
    // ms we give grace period before taking away scan button
    // prevents flickering
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final int SMOOTHING_DELAY = 3000;
    private HandlerThread smoothingHandlerThread;
    private Handler smoothingHandler;
    private Runnable smoothingRunnable;
    public static CodeScanner codeScanner;
    private QrCodeProcessor camera;
    private String playerId;
    private Button scanButton;
    private String currentCodeValue;

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

        smoothingHandlerThread = new HandlerThread(TAG);
        smoothingHandlerThread.start();
        smoothingHandler = new Handler(smoothingHandlerThread.getLooper());
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

        scanButton = root.findViewById(R.id.start_scan);
        scanButton.setOnClickListener(__ -> {
            if (smoothingRunnable != null) {
                smoothingHandler.removeCallbacks(smoothingRunnable);
                smoothingRunnable = null;
            }
            camera = new QrCodeProcessor(FragmentScanner.this, currentCodeValue, playerId);
            camera.processQrCode();
        });
        disableScanButton();
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        codeScanner = new CodeScanner(activity, scannerView);
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setFlashEnabled(false);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);

        codeScanner.setDecodeCallback(result -> activity.runOnUiThread(() -> {
            decodeCallback(result);
        }));

        codeScanner.setErrorCallback(thrown -> Log.e(TAG, "Camera has failed: ", thrown));
        return root;
    }

    private void decodeCallback(Result result) {
        currentCodeValue = Arrays.toString(result.getRawBytes());
        Log.d(TAG, String.format("decoded code: %s, enabling button for %d", currentCodeValue, SMOOTHING_DELAY));
        enableScanButton();
        if (smoothingRunnable != null) {
            smoothingHandler.removeCallbacks(smoothingRunnable);
        }
        smoothingRunnable = (() -> {
            Log.d(TAG, "disabling scan button");
            disableScanButton();
        });
        smoothingHandler.postDelayed(smoothingRunnable, SMOOTHING_DELAY);
    }

    public void disableScanButton() {
        scanButton.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
        scanButton.setClickable(false);
    }

    public void enableScanButton() {
        scanButton.getBackground().setColorFilter(null);
        scanButton.setClickable(true);
    }

    /**
     * Handles when the state is resumed (starts camera previous)
     */
    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    /**
     * Handles when the state is paused (release resources)
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