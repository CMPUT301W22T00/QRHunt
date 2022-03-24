package com.bigyoshi.qrhunt.qr;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

/**
 * Definition: Library to keep track of QR codes scanned by a certain player
 * Note: NA
 * Issues: TBA
 */
public class QRLibrary {

    private HashMap<String, PlayableQRCode> qrCodes;
    private String playerId;
    private FirebaseFirestore db;
    private double lat;
    private double lon;
    private String qrHash;
    private int score;
    private PlayableQRCode qrCode;

    /**
     * Finds player in database by ID and grabs all QR codes associated w/ them
     *
     * @param db       Player's QRDatabase
     * @param playerId Current player
     */
    public QRLibrary(FirebaseFirestore db, String playerId){
        qrCodes = new HashMap<>();
        if (playerId != null) {
        this.playerId = playerId;
        } else {
            this.playerId = "2Fcbb5fcf4-004b-4fff-8cbb-c7ed0b8749a7";
        }
        this.db = db;
        update();
    }

    /**
     * Updates the QRLibrary of the player (aligning to the their QR database)
     *
     */
    public void update() {
        Query qrList =  db.collection("users").document(playerId).collection("qrCodes");
        qrList.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (doc.exists()) {
                        qrCode = doc.toObject(PlayableQRCode.class);
                        qrCodes.put(doc.getId(), qrCode);
                    }
                }
            }
        });
    }

    public HashMap<String, PlayableQRCode> getQrCodes() { return qrCodes; }

    /**
     * Sorts all QRs in library from lowest to highest scoring
     *
     */
    public void sortLowestToHighest(){
        /* Integer in HashMap would either be the value of the QRCode
        or just some sort of order we use to rank the QRCodes (ie the values)
         */
    }

    /**
     * Sorts all QRs in Library from highest to lowest scoring
     *
     */
    public void sortHighestToLowest(){
        /* Integer in HashMap would either be the value of the QRCode
        or just some sort of order we use to rank the QRCodes (ie the values)
         */
    }
}