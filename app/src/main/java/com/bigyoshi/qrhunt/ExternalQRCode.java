package com.bigyoshi.qrhunt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Objects;

public class ExternalQRCode extends QRCode {
    private String id; // Hash of the actual data from the scan
    private int value; // The score of the QR code
    private int latitude;
    private int longitude;
    private int numScanned;
    private String image64;
    private HashMap<String, Object> qrStuff;


    // We need to distinguish QRCodes already scanned and those who have not been scanned yet
    //  Since initialization of numScanned would either be an update OR just 1
    public ExternalQRCode(int longitude, int latitude, int value, String id, FirebaseFirestore db, String userId){
        this.value = value;
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.AddQRCode(db, userId);
        this.numScanned = 1;
    }

    // Just a bunch of getters and setters, delete if unneeded
    public int getNumScanned() { return this.numScanned; }

    public void updateNumScanned(FirebaseFirestore db){
        Query containsId = db.collectionGroup("qrCodes").whereEqualTo("id", this.id);
        this.numScanned = Objects.requireNonNull(containsId.get().getResult()).size();
    }

    public int getValue() { return this.value; }

    public void setValue(int value) { this.value = value; }

    public int getLatitude() { return latitude; }

    public void setLatitude(int latitude) { this.latitude = latitude; }

    public int getLongitude() { return longitude; }

    public void setLongitude(int longitude) { this.longitude = longitude; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getImage64() { return this.image64; }

    public void setImage64(String image64) { this.image64 = image64; }

    public void AddQRCode(FirebaseFirestore db, String userId) {
        // ADDS QR CODE TO PLAYER?
        qrStuff = new HashMap<>();
        qrStuff.put("lat", this.latitude);
        qrStuff.put("long", this.longitude);
        qrStuff.put("score", this.value);
        qrStuff.put("id", this.id);

        db.collection("users").document(userId)
                .collection("qrCodes").document(this.id)
                .set(qrStuff)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("ADD_QR", "Successfully added QR to player");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ADD_QR", "Error adding QR to player", e);
                    }
                });
    }

    public void DeleteQRCode(FirebaseFirestore db, String userId) {
        db.collection("user").document(userId)
                .collection("qrCodes").document(this.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Delete_QR", "Successfully removed QR from player");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Delete_QR", "Error removing QR from player", e);
                    }
                });
    }
}
