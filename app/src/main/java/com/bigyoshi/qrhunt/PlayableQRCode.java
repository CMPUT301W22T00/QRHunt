package com.bigyoshi.qrhunt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.Objects;

/**
 * Definition: Playing QR - Players can scan this and get points
 * Note: Stores and pulls values related to the QR from database
 * Issues: TBA
 */
public class PlayableQRCode {
    private static final String TAG = PlayableQRCode.class.getSimpleName();
    private String id; // Hash of the actual data from the scan
    private int score; // The score of the QR code
    private QRLocation location;
    private int numScanned;
    private String imageUrl;
    private HashMap<String, Object> qrStuff;

    /**
     * Constructor method
     *
     * @param id    QRCode id
     * @param score QRCode score
     */
    /* We need to distinguish QRCodes already scanned and those who have not been scanned yet
        Since initialization of numScanned would either be an update OR just 1

     */
    public PlayableQRCode(String id, int score) {
        this.score = score;
        this.id = id;
        this.numScanned = 1;
    }

    /**
     * Getter method
     *
     * @return numScanned
     */
    // Just a bunch of getters and setters, delete if unneeded
    public int getNumScanned() {

        return this.numScanned;
    }

    /**
     * Getter method
     *
     * @return score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Getter method
     *
     * @return location
     */
    public QRLocation getLocation() {
        return this.location;
    }


    /**
     * Getter method
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter method
     *
     * @param lat latitude
     * @param lon longitude
     */
    public void setLocation(double lat, double lon) {
        this.location = new QRLocation(lat, lon);
    }

    /**
     * Setter method
     *
     * @param url QR image
     */
    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    /**
     * Getter method
     *
     * @return String url of image
     */
    public void getImageUrl(String url) {

    }

    /**
     * Checks if the location is null
     *
     * @return location
     */
    public boolean isLocation() {
        return this.location != null;
    }

    /**
     * Getter method for the database - pulls total number scanned
     *
     * @param db qrMetadata collection
     */
    public void grabNumScanned(FirebaseFirestore db) {
        // Pulls the total number scanned from the db
        Task<DocumentSnapshot> qrData = db.collection("qrCodes").document(this.id).get();
        this.numScanned = Integer.parseInt((Objects.requireNonNull
                (Objects.requireNonNull(qrData.getResult())
                        .getString("numScanned"))));
    }

    /**
     * Deletes QR from QR profile and database
     *
     * @param db       qrMetadata collection
     * @param playerId Current player
     */
    public void DeleteFromDB(FirebaseFirestore db, String playerId) {
        db.collection("users").document(playerId)
                .collection("qrCodes").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("user").document(playerId)
                                .collection("qrCodes").document(id).delete();
                        Log.d(TAG, "Successfully removed QR from data base");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error removing QR from data base", e);
                    }
                });

    }

    /**
     * Adds QR to QR profile and database
     *
     * @param db       qrMetadata collection
     * @param playerId Current player
     */
    public void addToDb(FirebaseFirestore db, String playerId) {
        qrStuff = new HashMap<>();
        qrStuff.put("image", imageUrl);
        qrStuff.put("score", score);
        qrStuff.put("latitude", location.getLat());
        qrStuff.put("longitude", location.getLong());
        qrStuff.put("geoHash", location.getGeoHash());

        db.collection("users").document(playerId)
                .collection("qrCodes").document(id).set(qrStuff, SetOptions.merge());
    }
}
