package com.bigyoshi.qrhunt;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class QRLibrary {

    private HashMap<String, ExternalQRCode> qrCodes;
    private String playerId;
    private FirebaseFirestore db;
    private double lat;
    private double lon;
    private String qrHash;
    private int score;
    private ExternalQRCode qrCode;

    public QRLibrary(FirebaseFirestore db, String playerId){
        qrCodes = new HashMap<>();
        this.playerId = playerId;
        this.db = db;

        Query qrList =  db.collection("user").document(playerId).collection("qrCodes");
        qrList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (doc.exists()) {
                            lat = doc.getDouble("latitude");
                            lon = doc.getDouble("longitude");
                            score = Integer.valueOf(doc.getString("score"));
                            qrHash = doc.getId();
                            qrCode = new ExternalQRCode(qrHash, score);
                            qrCode.setLocation(lat, lon);
                            qrCodes.put(qrHash, qrCode);

                        }
                    }
                }
            }
        });
    }

    public void sortLowestToHighest(){
        // Integer in HashMap would either be the value of the QRCode or just some sort of order we use to rank the QRCodes (ie the values)
    }

    public void HighestToLowest(){
        // Integer in HashMap would either be the value of the QRCode or just some sort of order we use to rank the QRCodes (ie the values)
    }
}
