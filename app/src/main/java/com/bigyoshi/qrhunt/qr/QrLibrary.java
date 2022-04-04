package com.bigyoshi.qrhunt.qr;

import com.bigyoshi.qrhunt.player.Player;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Definition: Library to keep track of QR codes scanned by a certain player
 * Note: NA
 * Issues: TBA
 */
public class QrLibrary implements Serializable {

    private String playerId;
    private transient FirebaseFirestore db;
    private transient ArrayList<PlayableQrCode> qrCodesList;
    private int scoreSorted; // 1 -> high-low, -1 -> low-high

    /**
     * Finds player in database by ID and grabs all QR codes associated w/ them
     *
     * @param db       Player's QRDatabase
     * @param playerId Current player
     */
    public QrLibrary(FirebaseFirestore db, String playerId){
        this.playerId = playerId;
        this.db = db;
        update();
    }

    /**
     * Updates the QRLibrary of the player (aligning to the their QR database)
     *
     */
    public void update() {
        qrCodesList = new ArrayList<>();
        Query qrList =  db.collection("users").document(playerId).collection("qrCodes");
        qrList.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (doc.exists()) {
                        PlayableQrCode qrCode = doc.toObject(PlayableQrCode.class);
                        qrCodesList.add(qrCode);
                    }
                }
                qrCodesList.sort(compareByScore);
            }
        });
        scoreSorted = 1;
    }

    public ArrayList<PlayableQrCode> getQrCodes() {
        return qrCodesList;
    }

    /**
     * Sorts all QRs in library from lowest to highest scoring
     *
     */
    public ArrayList<PlayableQrCode> sortScoreDescending(){
        /* Integer in HashMap would either be the value of the QRCode
        or just some sort of order we use to rank the QRCodes (ie the values)
         */
        qrCodesList.sort(compareByScore);
        scoreSorted = 1;
        return qrCodesList;

    }

    /**
     * Sorts all QRs in Library from highest to lowest scoring
     *
     */
    public ArrayList<PlayableQrCode> sortScoreAscending(){
        /* Integer in HashMap would either be the value of the QRCode
        or just some sort of order we use to rank the QRCodes (ie the values)
         */
        qrCodesList.sort(compareByScore.reversed());
        scoreSorted = -1;
        return qrCodesList;
    }

    public int getScoredSorted() {
        return scoreSorted;
    }

    Comparator<PlayableQrCode> compareByScore = new Comparator<PlayableQrCode>() {
        @Override
        public int compare(PlayableQrCode qr1, PlayableQrCode qr2) {
            return qr2.getScore() - qr1.getScore();
        }
    };


}
