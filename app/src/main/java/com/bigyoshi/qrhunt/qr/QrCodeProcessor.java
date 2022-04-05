package com.bigyoshi.qrhunt.qr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.ScanMode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Definition: Interprets the information that the scanner scans and gets the current location Note:
 * Gives the ability to take a picture for the QR
 * Notes: NA
 * Issues: TBA
 */
public class QrCodeProcessor {
    public static final String TAG = QrCodeProcessor.class.getSimpleName();
    private final Fragment frag;
    private final String qrContent;
    private byte[] digest;
    private LocationCallback hackyLocationCallback; // todo rename this maybe
    FusedLocationProviderClient client;
    public String hash;
    private String playerId;
    public int score;
    public FusedLocationProviderClient fusedLocationClient;
    private Context activity;

    /**
     * Constructor method
     *
     * @param frag     TBA
     * @param text     QR Content
     * @param playerId
     */
    public QrCodeProcessor(Fragment frag, String text, String playerId) {
        this.frag = frag;
        this.qrContent = text;
        this.playerId = playerId;
        activity = frag.getActivity();
        client = LocationServices.getFusedLocationProviderClient(activity);
        startPollingLocation();
    }

    /**
     * Gets player's geolocation
     */
    public void startPollingLocation() {

        /* alex please forgive me
        super, super hacky way to get location not to be null.
        apparently just requesting it is enough to get the location manager off it's ass
        even if we never care about the result
        it's super messed up
        if it break in the future, we may need to implement more of LocationCallback
        but it's working now
        https://stackoverflow.com/questions/29441384/fusedlocationapi-getlastlocation-always-null/29854418#29854418
         */

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        hackyLocationCallback = new LocationCallback() {
        };
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(mLocationRequest, hackyLocationCallback, null);
    }

    /**
     * Processes QR code to be added
     */
    public void processQrCode() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] qrCodeParts = qrContent.split(":");
        if (qrCodeParts[0].matches("qrhunt")) {
            LocationServices.getFusedLocationProviderClient(activity)
                    .removeLocationUpdates(hackyLocationCallback);
            if (qrCodeParts[1].matches("shareprofile")) {
                Toast.makeText(activity, "Sharing user's profile", Toast.LENGTH_SHORT).show();
                InternalQrCode shareProfileQr = new InternalQrCode(qrCodeParts[2], false, frag, playerId);
            } else {
                Toast.makeText(activity, "Transferring your data...", Toast.LENGTH_SHORT).show();
                InternalQrCode transferProfileQr = new InternalQrCode(qrCodeParts[2], true, frag, playerId);
            }
        } else {
            computeHash();
            computeScore();
            PlayableQrCode qrCode = new PlayableQrCode(playerId, hash, score);
            getLocation()
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Location location = task.getResult();
                                    if (location != null) {
                                        qrCode.setLocation(new QrLocation(location));
                                    }
                                /* this stops listening to the updates that
                                we didn't actually care about in the first place for; see startPollingLocation
                                 */
                                    LocationServices.getFusedLocationProviderClient(activity)
                                            .removeLocationUpdates(hackyLocationCallback);

                                    FragmentAddQrCode addQrCode = FragmentAddQrCode.newInstance(qrCode);
                                    addQrCode.show(
                                            frag.getChildFragmentManager(), FragmentAddQrCode.TAG);
                                }
                            });
        }
    }

    /**
     * Hashes the QR code
     */
    private void computeHash() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException noAlgEx) {
            noAlgEx.printStackTrace();
        }
        digest = messageDigest.digest(qrContent.getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : digest) {
            stringBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        hash = stringBuilder.toString();
    }

    /**
     * Calculates QR score
     */
    private void computeScore() {
        // Uniform RV value between 0 and 1
        score = new BigInteger(1, digest)
                .multiply(new BigInteger("100"))
                .divide((new BigInteger("2").pow(160)))
                .intValue();
    }

    /**
     * Gets location if permission is enabled(?)
     *
     * @return client.getLastLocation()
     */
    private Task<Location> getLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return client.getLastLocation();
    }
}


