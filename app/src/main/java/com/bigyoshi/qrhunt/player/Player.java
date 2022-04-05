package com.bigyoshi.qrhunt.player;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bigyoshi.qrhunt.qr.QrLibrary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Definition: Object representing player, keeps track of all player data and deals with db functions regarding players
 * Note: NA
 * Issues: NA
 */
public class Player implements Serializable {
    private static final String TAG = Player.class.getSimpleName();


    protected transient FirebaseFirestore db;
    protected transient CollectionReference collectionReference;

    protected int totalScore;
    protected final RankInfo rankInfo;
    protected final BestQr bestUniqueQr;
    protected final BestQr bestScoringQr;
    protected String username;
    protected Contact contact;
    protected Boolean admin;
    protected String playerId;
    // TODO: fix this. In theory, QrLibrary is _perfectly_ serializable. The android runtime disagrees
    // we're leaving it this way for now because confusingly, everything seems to work
    public transient QrLibrary qrLibrary;

    protected final transient Context context;
    /**
     * Constructor method
     *
     * @param context context
     */
    public Player(String playerId, Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");

        rankInfo = new RankInfo();
        bestScoringQr = new BestQr();
        bestUniqueQr = new BestQr();
        this.playerId = playerId;
        this.totalScore = 0;
        this.username = "";
        this.admin = false;
        this.contact = new Contact();
        this.qrLibrary = new QrLibrary(db, Optional.ofNullable(playerId).orElse(getPlayerId()));
    }

    public static Player fromPlayerId(String playerId) {
        Player player = new Player(playerId, null);
        player.initialize();
        return player;
    }

    public static Player fromDoc(DocumentSnapshot doc) {
        Player player = new Player(doc.getId(), null);
        player.setPropsFromDoc(doc);
        return player;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public BestQr getBestUniqueQr() {
        return bestUniqueQr;
    }

    public BestQr getBestScoringQr() {
        return bestScoringQr;
    }

    public RankInfo getRankInfo() {
        return rankInfo;
    }

    /**
     * Getter method
     *
     * @return Contact contact
     */
    public Contact getContact() {
        return this.contact;
    }

    /**
     * Getter method
     *
     * @return Integer score
     */
    public int getTotalScore() {
        return this.totalScore;
    }

    /**
     * Getter method
     *
     * @return String username
     */
    public String getUsername() {
        return this.username;
    }


    /**
     * Setter method (both email and social media contact)
     *
     * @param contact
     */
    public void setContact(Contact contact) {
        // Use the editTextId to identify which contact to update (with toUpdate)
        this.contact.setSocial(contact.getSocial());
        this.contact.setEmail(contact.getEmail());
    }

    /**
     * Setter method
     *
     * @param newName new username to assign
     */
    public void setUsername(String newName) {
        this.username = newName;
    }

    /**
     * Assigns another player admin if this player is admin
     *
     * @param newAdmin new Admin to assign
     * @return String describing whether or not player was made admin
     */
    public String makeAdmin(Player newAdmin) {
        if (this.isAdmin()) {
            newAdmin.admin = true;
            return "PlayerInfo now admin.";
        } else {
            newAdmin.admin = false;
            return "PlayerInfo did not become admin.";
        }
    }


    /**
     * Checks whether a player is an admin
     *
     * @return Boolean representing whether a player is an admin or not
     */
    public Boolean isAdmin() {
        return admin;
    }

    /**
     * Saves PlayerData in HashMap to database associated with playerId
     */
    public void savePlayer() {
        HashMap<String, Object> playerData = new HashMap<>();
        if (playerId.length() > 0) {
            // If there’s some data in the EditText field, then we create a new key-value pair.
            playerData.put("admin", this.admin);
            playerData.put("contact", this.contact);
            playerData.put("totalScore", this.totalScore);
            playerData.put("username", this.username);
            // The set method sets a unique id for the document
            collectionReference
                    .document(playerId)
                    .set(playerData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // These are a method which gets executed when the task is succeeded

                            Log.d(TAG, "Data has been added successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // These are a method which gets executed if there’s any problem
                            Log.d(TAG, "Data could not be added!" + e.toString());
                        }
                    });
        }
    }

    /**
     * Gets playerData from database by matching against playerIds in database
     */
    public void initialize() {
        collectionReference.document(playerId).addSnapshotListener((EventListener<DocumentSnapshot>) (doc, error) -> {
            if (error == null && doc != null && doc.getData() != null) {
                setPropsFromDoc(doc);
            }
        });
    }

    private void setPropsFromDoc(DocumentSnapshot doc) {
        Log.d(TAG, String.valueOf(doc.getData().get("admin")));
        Log.d(TAG, String.valueOf(doc.getData().get("contact")));
        Log.d(TAG, String.valueOf(doc.getData().get("totalScore")));
        Log.d(TAG, String.valueOf(doc.getData().get("username")));

        admin = (Boolean) doc.getData().get("admin");

        HashMap<String, String> contactMap = (HashMap<String, String>)
                doc.getData().get("contact");
        if (contact != null) {
            contact.setEmail(contactMap.get("email"));
            contact.setSocial(contactMap.get("social"));
        }

        Map<String, Long> rankInfoMap = (HashMap<String, Long>) doc.get("rank");
        if (rankInfoMap != null) {
            rankInfo.setTotalScannedRank(Math.toIntExact(rankInfoMap.getOrDefault("totalScanned", Long.valueOf(1))));
            rankInfo.setBestUniqueQrRank(Math.toIntExact(rankInfoMap.getOrDefault("bestUniqueQr", (Long.valueOf(1)))));
            rankInfo.setTotalScoreRank(Math.toIntExact(rankInfoMap.getOrDefault("totalScore", (Long.valueOf(1)))));
        }
        // code duplication is cool 😎
        Map<String, Object> bestScoringQrMap = (HashMap<String, Object>) doc.get("bestScoringQr");
        if (bestScoringQrMap != null) {
            bestScoringQr.setQrId((String) bestScoringQrMap.getOrDefault("qrId", null));
            bestScoringQr.setScore(Math.toIntExact((Long) bestScoringQrMap.getOrDefault("score", 0)));
        }
        Map<String, Object> bestUniqueQrMap = (HashMap<String, Object>) doc.get("bestUniqueQr");
        if (bestUniqueQrMap != null) {
            bestUniqueQr.setQrId((String) bestUniqueQrMap.getOrDefault("qrId", null));
            bestUniqueQr.setScore(Math.toIntExact((Long) bestUniqueQrMap.getOrDefault("score", 0)));
        }
        totalScore = Math.toIntExact((long) doc.getData().get("totalScore"));
        username = (String) doc.getData().get("username");
    }

    /**
     * Updates db within the document associated with playerId
     */
    public void updateDB() {
        collectionReference
                .document(playerId)
                .update("admin", this.admin);
        collectionReference
                .document(playerId)
                .update("contact", this.contact);
        collectionReference
                .document(playerId)
                .update("totalScore", this.totalScore);
        collectionReference
                .document(playerId)
                .update("username", this.username);
    }
}
