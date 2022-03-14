package com.bigyoshi.qrhunt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bigyoshi.qrhunt.bottom_navigation.map.MapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class QRGeoPins extends MapFragment {

    private ArrayList<OverlayItem> geoPins;
    private MyLocationNewOverlay mLocationOverlay;
    private FirebaseFirestore db;

    public QRGeoPins(ArrayList<OverlayItem> geoPins){
        this.geoPins = geoPins;
    }

    public void getQRLocation(){
        super.mLocationOverlay.getMyLocation();

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;

        db = FirebaseFirestore.getInstance();
        //Task<QuerySnapshot> qrCodes = db.collection("users").document().collection("qrCodes").get().forEach;
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Task<QuerySnapshot> latitude = document.getReference().collection("qrCodes").get(Source.valueOf("latitude"));
                                Task<QuerySnapshot> longitude = document.getReference().collection("qrCodes").get(Source.valueOf("longitude"));
                                        // ARRAY LIST STUFF HERE
                                        Log.d("MONKE", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("MONKE", "Error getting documents: ", task.getException());
                        }
                    }
                });

        //geoPins.add(new OverlayItem("", "", new GeoPoint()));
    }







}
