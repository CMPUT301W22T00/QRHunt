package com.bigyoshi.qrhunt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class AugmentedCamera {
    private final Fragment frag;
    public String hash;
    public int value;
    //public QRLocation qrLocation;
    private double[] location;
    public FusedLocationProviderClient fusedLocationClient;

    public AugmentedCamera(Fragment frag, String text) {
        this.frag = frag;
        scanQRCode(text);
        //getLocation();
        Random rd1 = new Random();
        Random rd2 = new Random();
        location = new double[2];
        location[0] = 53.5232 + rd1.nextDouble();
        location[1] = 113.5263 + rd2.nextDouble();
        //new AddQRCodeFragment(hash, value, qrLocation).show(this.frag.getChildFragmentManager(), "ADD QR");
        new AddQRCodeFragment(hash, value, location[0], location[1]).show(this.frag.getChildFragmentManager(), "ADD QR");

    }

    private void scanQRCode(String scannedCode) {
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

    private Task<Location> getLocation() {
        if (ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(frag.getActivity());
        return fusedLocationClient.getLastLocation();
    }
}
