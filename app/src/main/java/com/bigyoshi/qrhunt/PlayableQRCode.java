package com.bigyoshi.qrhunt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.Serializable;
import java.util.Objects;

/**
 * Definition: Playing QR - Players can scan this and get points
 * Note: Stores and pulls values related to the QR from database
 * Issues: TBA
 */
public class PlayableQRCode implements Serializable {
    public static final String TAG = PlayableQRCode.class.getSimpleName();
    private String id; // Hash of the actual data from the scan
    private String playerId;
    private Integer score;
    private QRLocation location;
    private String imageUrl;
    private int numScanned;
    // transient → will not be serialized
    private transient FirebaseFirestore db;


    public PlayableQRCode() {
        this.numScanned = 1;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Constructor method
     *
     * @param id    QRCode id
     * @param score QRCode score
     */
    /* We need to distinguish QRCodes already scanned and those who have not been scanned yet
        Since initialization of numScanned would either be an update OR just 1
     */
    public PlayableQRCode(String playerId, String id, int score) {
        this.playerId = playerId;
        this.id = id;
        this.score = score;
        this.numScanned = 1;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Getter method
     *
     * @return numScanned
     */
    @Exclude
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
     * @return id
     */
    @DocumentId
    public String getId() {
        return id;
    }

    /**
     * Setter method
     *
     * @param imageUrl QR image
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Getter method
     *
     * @return String url of image
     */
    public String getImageUrl(String url) {
        return imageUrl;
    }

    /**
     * Getter method for the database - pulls total number scanned
     *
     * @param db qrMetadata collection
     */
    public void grabNumScanned(FirebaseFirestore db) {
        // Pulls the total number scanned from the db
        Task<DocumentSnapshot> qrData = db.collection("qrCodes").document(getId()).get();
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
    public void deleteFromDb(FirebaseFirestore db, String playerId) {
        // todo: toast here for confirmation either way?
        db.collection("users").document(playerId)
                .collection("qrCodes").document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    db.collection("user").document(playerId)
                            .collection("qrCodes").document(id).delete();
                    Log.d(TAG, "Successfully removed QR from data base");
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error removing QR from data base", e));

    }

    /**
     * Adds QR to QR profile and database
     *
     */
    public void addToDb() {
        db.collection("users").document(getPlayerId())
                .collection("qrCodes").document(getId()).set(this);
    }

    @Exclude
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public QRLocation getLocation() {
        return location;
    }

    public void setLocation(QRLocation location) {
        this.location = location;
    }
}
