package com.bigyoshi.qrhunt;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.ScanMode;

/**
 * Definition: Scanner with camera - Scans and decodes QR code
 * Note: NA
 * Issues: TBA
 */
public class FragmentScanner extends Fragment {
    public static final String TAG = FragmentScanner.class.getSimpleName();
    private CodeScanner codeScanner;
    private QRCodeProcessor camera;
    private String playerId;

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

        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.scanner_fragment, container, false);

        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        assert activity != null;
        codeScanner = new CodeScanner(activity, scannerView);

        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setFlashEnabled(false);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);

        ImageButton button = root.findViewById(R.id.start_scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.processQRCode();
            }
        });

        codeScanner.setDecodeCallback(result -> activity.runOnUiThread(() -> {
            camera = new QRCodeProcessor(FragmentScanner.this, result.getText(), playerId);
            button.setVisibility(View.VISIBLE);
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
}

