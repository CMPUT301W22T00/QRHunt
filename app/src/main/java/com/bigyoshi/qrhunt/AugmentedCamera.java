package com.bigyoshi.qrhunt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Definition: Camera functionalities
 *
 *
 */
public class AugmentedCamera {
    private final Fragment frag;
    private final String qrContent;
    private byte[] digest;
    private LocationCallback hackyLocationCallback;
    FusedLocationProviderClient client;
    public String hash;
    private String playerId;
    public int score;
    public FusedLocationProviderClient fusedLocationClient;

    /**
     * Constructor
     * @param frag
     * @param text
     * @param playerId
     */
    public AugmentedCamera(Fragment frag, String text, String playerId) {
        this.frag = frag;
        this.qrContent = text;
        this.playerId = playerId;
        client = LocationServices.getFusedLocationProviderClient(frag.getActivity());
        pollLocation();
    }

    /**
     * Gets user's geolocation
     *
     */
    public void pollLocation() {
        // alex please forgive me
        // super, super hacky way to get location not to be null.
        // apparently just requesting it is enough to get the location manager off it's ass
        // even if we never care about the result
        // it's super messed up
        // if it break in the future, we may need to implement more of LocationCallback
        // but it's working now
        // https://stackoverflow.com/questions/29441384/fusedlocationapi-getlastlocation-always-null/29854418#29854418
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        hackyLocationCallback = new LocationCallback() {};
        if (ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(mLocationRequest, hackyLocationCallback, null);
    }

    /**
     * Processes QR code to be added
     *
     */
    public void processQRCode() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // todo check here: is this an internal code
        computeHash();
        PlayableQRCode qrCode = new PlayableQRCode(hash, score);
        computeScore();

        getLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    qrCode.setLocation(location.getLatitude(), location.getLongitude());
                }
                // this stops listening to the updates we didn't actually care about in the first place for
                // see pollLocation
                LocationServices.getFusedLocationProviderClient(frag.getActivity()).removeLocationUpdates(hackyLocationCallback);
                // todo: do we really need a whole wrapper class?
                // todo: handle cases where we can get the location gracefully
                new FragmentAddQRCode(hash, score, new QRLocation(location.getLatitude(), location.getLongitude()), playerId).show(frag.getChildFragmentManager(), "ADD QR");
            }
        });
    }

    /**
     * Hashes the QR code
     *
     */
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

    /**
     * Calculates QR score
     *
     */
    private void computeScore() {
        // Need to calculate score here
        // Probably have to pass in the hash or whatever we use to calculate the value
        // sha1 gives 160 bits → max value is therefore 2^160
        score = new BigInteger(1, digest).multiply(new BigInteger("100")).divide((new BigInteger("2").pow(160))).intValue();
    }

    /**
     * Gets location if permission is enabled(?)
     *
     * @return client.getLastLocation()
     */
    private Task<Location> getLocation() {
        if (ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(frag.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return client.getLastLocation();
    }

}
