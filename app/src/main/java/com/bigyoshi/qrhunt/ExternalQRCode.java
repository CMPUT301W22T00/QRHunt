package com.bigyoshi.qrhunt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExternalQRCode extends QRCode {
    private String id; // Hash of the actual data from the scan
    private int value; // The score of the QR code
    private int latitude;
    private int longitude;
    private int numScanned;
    private FirebaseFirestore db;
    public boolean isInDB;

    // We need to distinguish QRCodes already scanned and those who have not been scanned yet
    //  Since initialization of numScanned would either be an update OR just 1
    public ExternalQRCode(int longitude, int latitude, int value, String id){
        this.value = value;
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        // Checks if the database contains this id
        // If it does gets the num scanned from the database
        // Else it sets it to 1
        InDB(this.id);
        if (isInDB) {
            getNumScannedDB(this.id);
        } else {
            this.numScanned = 1;
        }
    }

    // Just a bunch of getters and setters
    public int getNumScanned() { return this.numScanned; }

    public void setNumScanned(int numScanned) { this.numScanned = numScanned; }

    public void updateNumScanned(){ this.numScanned += 1; }

    public int getValue() { return this.value; }

    public void setValue(int value) { this.value = value; }

    public int getLatitude() { return latitude; }

    public void setLatitude(int latitude) { this.latitude = latitude; }

    public int getLongitude() { return longitude; }

    public void setLongitude(int longitude) { this.longitude = longitude; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    private void InDB(String id) {
        db.collection("qrcodes").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                isInDB = true;
                                Log.d("QR_CODE", "QR is in the data base");
                            } else {
                                isInDB = false;
                                Log.d("QR_CODE", "QR is not in the data base");
                            }
                        } else {
                            Log.d("QR_CODE", "Error getting QR from database.", task.getException());
                        }
                    }
                });
    }

    private void getNumScannedDB(String id) {
        db.collection("qrcodes").document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ExternalQRCode qr = documentSnapshot.toObject(ExternalQRCode.class);
                        setNumScanned(qr.getNumScanned());
                    }
                });
    }
}
