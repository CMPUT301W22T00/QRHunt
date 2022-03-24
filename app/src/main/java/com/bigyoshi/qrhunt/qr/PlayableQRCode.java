package com.bigyoshi.qrhunt.qr;

import android.util.Log;

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

    /**
     * Constructor method
     *
     */
    public PlayableQRCode() {
        this.numScanned = 1;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Constructor method
     * Note: We need to distinguish QRCodes already scanned and those who have not been scanned yet
     *         Since initialization of numScanned would either be an update OR just 1
     *
     * @param id    QRCode id
     * @param score QRCode score
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
     * Getter method
     *
     * @return Player id
     */
    @Exclude
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Getter method
     *
     * @return QR Location
     */
    public QRLocation getLocation() {
        return location;
    }

    /**
     * Getter method
     *
     * @return Image URL
     */
    public String getImageUrl() {
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
     * Setter method
     *
     * @param playerId Player id to set
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     * Setter method
     *
     * @param location QR Location to set
     */
    public void setLocation(QRLocation location) {
        this.location = location;
    }

    /**
     * Setter method
     *
     * @param imageUrl Image URL to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

}
