package com.bigyoshi.qrhunt;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
     * @param db Player's QRDatabase
     * @param playerId Current player
     */
    public QRLibrary(FirebaseFirestore db, String playerId){
        qrCodes = new HashMap<>();
        if (playerId != null) {
        this.playerId = playerId;
        } else {
            this.playerId = "c6670e44-1fe2-4b98-acfd-98c55767cf3c";
        }
        this.db = db;
        update();
    }

    /**
     * Updates the QRLibrary of the player (aligning to the their QR database)
     */
    public void update() {
        Query qrList =  db.collection("users").document("2c5ed7c6-545a-4a8d-bb90-f817e004f4a8").collection("qrCodes");
        qrList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (doc.exists()) {
                            lat = doc.getDouble("latitude");
                            lon = doc.getDouble("longitude");
                            score = doc.getLong("score").intValue();
                            qrHash = doc.getId();
                            qrCode = new PlayableQRCode(qrHash, score);
                            qrCode.setLocation(lat, lon);
                            qrCodes.put(qrHash, qrCode);

                        }
                    }
                }
            }
        });
    }

    public HashMap<String, PlayableQRCode> getQrCodes() { return qrCodes; }

    /**
     * Sorts all QRs in library from lowest to highest scoring
     */
    public void sortLowestToHighest(){
        /* Integer in HashMap would either be the value of the QRCode
        or just some sort of order we use to rank the QRCodes (ie the values)
         */
    }

    /**
     * Sorts all QRs in Library from highest to lowest scoring
     */
    public void sortHighestToLowest(){
        /* Integer in HashMap would either be the value of the QRCode
        or just some sort of order we use to rank the QRCodes (ie the values)
         */
    }
}
