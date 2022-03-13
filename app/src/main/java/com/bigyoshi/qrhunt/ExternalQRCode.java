package com.bigyoshi.qrhunt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.LoadBundleTask;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.Objects;

public class ExternalQRCode extends QRCode {
    private String id; // Hash of the actual data from the scan
    private int value; // The score of the QR code
    private QRLocation location;
    private int numScanned;
    private String image64;
    private HashMap<String, Object> qrStuff;
    private HashMap<String, Object> playerQrStuff;
    private HashMap<String, Object> locationStuff;


    // We need to distinguish QRCodes already scanned and those who have not been scanned yet
    //  Since initialization of numScanned would either be an update OR just 1
    public ExternalQRCode(String id, int value){
        this.value = value;
        this.id = id;
        this.numScanned = 1;
    }

    // Just a bunch of getters and setters, delete if unneeded
    public int getNumScanned() { return this.numScanned; }

    public void grabNumScanned(FirebaseFirestore db){
        // Pulls the total number scanned from the db
        Task<DocumentSnapshot> qrData = db.collection("qrCodes").document(this.id).get();
        this.numScanned = Integer.parseInt((Objects.requireNonNull(Objects.requireNonNull(qrData.getResult()).getString("numScanned"))));
    }

    public int getValue() { return this.value; }

    public QRLocation getLocation() { return this.location; }

    public void setLocation(double lat, double lon) {
        this.location.setLat(lat);
        this.location.setLong(lon);
        this.location.updateId();
    }

    public boolean isLocation() {
        return this.location != null;
    }

    public String getId() { return id; }

    public String getImage64() { return this.image64; }

    public void setImage64(String image64) { this.image64 = image64; }

    public void AddToDB(FirebaseFirestore db) {
        // ADDS QR CODE TO DataBase
        DocumentReference qrPage = db.collection("qrCodes").document(this.id);
        boolean isLocation = this.isLocation();
        QRLocation location = this.location;
        if (isLocation) {
            locationStuff.put("latitude", location.getLat());
            locationStuff.put("longitude", location.getLong());
        }

        qrStuff = new HashMap<>();
        qrStuff.put("value", this.value);
        qrStuff.put("numScanned", this.numScanned);

        qrPage.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //qrStuff.put("numScanned", Integer.parseInt((Objects.requireNonNull(document.getString("numScanned")))) + 1);
                            } else {
                                qrPage.set(qrStuff)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("ADD_QR", "Successfully added QR to data base");
                                        }
                                    })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("ADD_QR", "Error adding QR to player", e);
                                            }
                                        });
                            }
                            if (isLocation) {
                                locationStuff = new HashMap<>();
                                qrPage.collection("locations").document(location.getId())
                                        .set(locationStuff, SetOptions.merge());
                            }
                        }
                    }
                });
        //grabNumScanned(db);
    }

    public void DeleteFromDB(FirebaseFirestore db) {
        db.collection("qrCodes").document(this.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Delete_QR", "Successfully removed QR from data base");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Delete_QR", "Error removing QR from data base", e);
                    }
                });
    }

    public void AddToQRLibrary(FirebaseFirestore db) {
        playerQrStuff = new HashMap<>();
        playerQrStuff.put("image", "hello");
        db.collection("users").document("04717e93-d613-46da-99e4-aa97e6fe8793")
                .collection("qrCodes").document(this.id).set(playerQrStuff, SetOptions.merge());
    }
}
