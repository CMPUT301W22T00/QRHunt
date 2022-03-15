package com.bigyoshi.qrhunt.bottom_navigation.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

//import com.bigyoshi.qrhunt.QRGeoPins;
import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentMapBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Definition: Map API
 *
 *
 */
public class MapFragment extends Fragment {
    private final String TAG = MapFragment.class.getSimpleName();
    private MapView map = null;
    private FragmentMapBinding binding;
    private MyLocationNewOverlay mLocationOverlay;
    private Marker geoPin;
    private FirebaseFirestore db;
    Double lat;
    Double lng;


    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Load/Initialize osmdroid configuration and database
        db = FirebaseFirestore.getInstance();
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inflate and create the map
        //setContentView(R.layout.fragment_map);
        map = (MapView) binding.mapview;
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Map Zoom Controls
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Follows user and centers on them
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.enableFollowLocation();

        // Change person icon
        Drawable directionIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_player_nav);
        Drawable personIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_icon);
        this.mLocationOverlay.setDirectionArrow(drawableToBitmap(personIcon), drawableToBitmap(directionIcon));

        // Adding the overlays
        map.getOverlays().add(this.mLocationOverlay);
        IMapController mapController = map.getController();
        mapController.setZoom(20);

        Query qrLocation =  db.collectionGroup("qrCodes");
        qrLocation.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        if (doc.exists()) {
                            lat = doc.getDouble("latitude");
                            lng = doc.getDouble("longitude");
                            if (lat != null && lng != null){
                                Log.d("Monke", String.format("lat %f long %f", lat, lng));
                                geoPin = new Marker(map);
                                geoPin.setPosition(new GeoPoint(lat, lng));
                                geoPin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(geoPin);
                            }
                        } else {
                            Log.d("Monke", "No such document");
                        }
                    }
                } else {
                    Log.d("Monke", "get failed with ", task.getException());
                }
            }
        });

        return root;
    }


    /**
     * ...?
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Refreshes the osmdroid configuration on resuming
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        //Configuration.getInstance().load(this.getContext(), PreferenceManager.getDefaultSharedPreferences(this.getContext()));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    /**
     * Refreshes the osmdroid configuration on resuming
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        //Configuration.getInstance().save(this.getContext(), prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    /**
     * Converts a vector image to a bitmap
     * @param drawable
     *        - A vector image
     * @return bitmap of the vector image
     */
    public static Bitmap drawableToBitmap (Drawable drawable){
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}