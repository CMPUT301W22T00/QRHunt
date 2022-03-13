package com.bigyoshi.qrhunt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;

public class AugmentedCamera {
    private final Fragment frag;
    private final String qrContent;
    private byte[] digest;
    public String hash;
    public int score;
    public QRLocation qrLocation;
    public FusedLocationProviderClient fusedLocationClient;

    public AugmentedCamera(Fragment frag, String text) {
        this.frag = frag;
        this.qrContent = text;
    }

    public void processQRCode() {
        // Scans QRCode -> reads whether it is a internal or external -> makes it either external or internal
        // -> If external -> calculate the value -> save into db -> player sets up QRProfile
        // -> If internal, show the game status (as a pop up) or log-in to the account
        computeHash();
        ExternalQRCode qrCode = new ExternalQRCode(hash, score);
        computeScore();

        getLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    qrCode.setLocation(location.getLatitude(), location.getLongitude());
                }
                new AddQRCodeFragment(hash, score, qrLocation).show(frag.getChildFragmentManager(), "ADD QR");
            }
        });
    }

    private void computeHash() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        digest = md.digest(qrContent.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        hash = sb.toString();
    }

    private void computeScore() {
        // Need to calculate score here
        // Probably have to pass in the hash or whatever we use to calculate the value
        // sha1 gives 160 bits → max value is therefore 2¹⁶⁰
        score = new BigInteger(1, digest).multiply(new BigInteger("100")).divide((new BigInteger("2").pow(160))).intValue();
    }

    private void capturePhoto() {
        // Saves the photo and correlates it to QRProfile
    }

    private Task<Location> getLocation() {
        if (ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(frag.getActivity());
        return fusedLocationClient.getLastLocation();
    }
}
