package com.bigyoshi.qrhunt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class QRLibrary {
    private HashMap<Integer, ExternalQRCode> qrCodes;

    public void sortLowestToHighest(){
        // Integer in HashMap would either be the value of the QRCode or just some sort of order we use to rank the QRCodes (ie the values)
    }

    public void HighestToLowest(){
        // Integer in HashMap would either be the value of the QRCode or just some sort of order we use to rank the QRCodes (ie the values)
    }

    public void AddQRCode(FirebaseFirestore db, ExternalQRCode scannedQR, Player player) {
        qrCodes = new HashMap<>();
        Integer num = scannedQR.getValue();
        String qrID = scannedQR.getId();
        String playerID = player.getPlayerInfo().getUniqueKey();
        qrCodes.put(num, scannedQR);
        db.collection("users").document("players")
                .collection(playerID).document("QRLibrary")
                .collection("QRCodes").document(qrID).set(qrCodes, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // No idea on which TAG to import - Allan
                        Log.d("ADD_QR", "Successfully added QR to player QR Library");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Same here - Allan
                        Log.w("ADD_QR", "Error adding QR to player QR Library", e);
                    }

                });

    }

    public void DeleteQRCode(FirebaseFirestore db, ExternalQRCode deleteQR, Player player) {
        db.collection("users").document("players")
                .collection(player.getPlayerInfo().getUniqueKey()).document("QRLibrary")
                .collection("QRCodes").document(deleteQR.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Delete_QR", "Successfully removed QR from QR library");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Delete_QR", "Error removing QR from QRLibrary", e);
                    }
                });
    }
}
