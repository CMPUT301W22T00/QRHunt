package com.bigyoshi.qrhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class AugmentedCamera {
    private final Activity activity;
    public String hash;
    public int value;
    public QRLocation qrLocation;
    public FusedLocationProviderClient fusedLocationClient;

    public AugmentedCamera(Activity activity, String text) {
        this.activity = activity;
        scanQRCode(text);
        getLocation();
        // can't seem to call the support fragment manager;
        // new AddQRCodeFragment(hash, value, qrLocation).show(getSupportFragmentManager(), "ADD QR");
        ///////////////////////////////////////////////////////////////////
        // testing
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ExternalQRCode qrCode = new ExternalQRCode(hash, value);
        //qrCode.setLocation(qrLocation.getLat(), qrLocation.getLong());
        qrCode.AddToDB(db);
        qrCode.AddToQRLibrary(db);
        ////////////////////////////////////////////////////////////////

    }

    public void scanQRCode(String scannedCode) {
        // Scans QRCode -> reads whether it is a internal or external -> makes it either external or internal
        // -> If external -> calculate the value -> save into db -> player sets up QRProfile
        // -> If internal, show the game status (as a pop up) or log-in to the account

        // Currently does not check if the QR is external or internal
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        byte[] digest = md.digest(scannedCode.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        hash = sb.toString();
        calculateValue(digest);
    }

    private void calculateValue(byte[] digest) {
        // Need to calculate score here
        // Probably have to pass in the hash or whatever we use to calculate the value
        // sha1 gives 160 bits → max value is therefore 2¹⁶⁰
        value = new BigInteger(1, digest).multiply(new BigInteger("100")).divide((new BigInteger("2").pow(160))).intValue();
    }

    private void capturePhoto() {
        // Saves the photo and correlates it to QRProfile
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                    qrLocation = new QRLocation(location.getLatitude(), location.getLongitude());
                } else {
                    Toast.makeText(activity, "Last known location is not available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
