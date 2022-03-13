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
    private Location location;
    private int numScanned;
    private String image64;
    private HashMap<String, Object> qrStuff;
    private HashMap<String, Object> locationStuff;


    // We need to distinguish QRCodes already scanned and those who have not been scanned yet
    //  Since initialization of numScanned would either be an update OR just 1
    public ExternalQRCode(int value, String id){
        this.value = value;
        this.id = id;
        this.numScanned = 1;
    }

    // Just a bunch of getters and setters, delete if unneeded
    public int getNumScanned() { return this.numScanned; }

    public void updateNumScanned(FirebaseFirestore db){
        //not working
        Query containsId = db.collectionGroup("qrCodes").whereEqualTo("id", this.id);
        this.numScanned = Objects.requireNonNull(containsId.get().getResult()).size();
    }

    public int getValue() { return this.value; }

    public Location getLocation() { return this.location; }

    public void setLocation(double lat, double lon) {
        this.location.setLat(lat);
        this.location.setLon(lon);
        this.location.updateId();
    }

    public boolean isLocation() {
        if (this.location != null) { return true; }
        return false;
    }

    public String getId() { return id; }

    public String getImage64() { return this.image64; }

    public void setImage64(String image64) { this.image64 = image64; }

    public void AddQRCodeDB(FirebaseFirestore db) {
        // ADDS QR CODE TO DataBase
        DocumentReference qrPage = db.collection("qrCodes").document(this.id);
        boolean isLocation = this.isLocation();
        Location location = this.location;
        if (isLocation) {
            locationStuff.put("Latitude", location.getLat());
            locationStuff.put("Longitude", location.getLon());
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
                                qrStuff.put("numScanned", Integer.parseInt((Objects.requireNonNull(task.getResult().getString("numScanned")))) + 1);
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
                                qrPage.collection("Locations").document(location.getId())
                                        .set(locationStuff, SetOptions.merge());
                            }
                        }
                    }
                });
    }

    public void DeleteQRCodeDB(FirebaseFirestore db, String userId) {
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
}
