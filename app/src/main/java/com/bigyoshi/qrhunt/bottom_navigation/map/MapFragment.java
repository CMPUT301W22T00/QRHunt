package com.bigyoshi.qrhunt.bottom_navigation.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
// import com.bigyoshi.qrhunt.QRGeoPins;
import com.bigyoshi.qrhunt.PlayableQRCode;
import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

/**
 * Definition: Fragment representing the map Note: N/A Issues: Slow to load
 * https://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = MapFragment.class.getSimpleName();
    private static final String KEY_VIEWPORT_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final Integer DEFAULT_ZOOM = 15;

    private MapView mapView;
    FragmentMapBinding binding;
    private FirebaseFirestore db;
    private GoogleMap googleMap;
    private Location lastKnownLocation;
    private Parcelable viewportPosition;

    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     *
     * @param inflater inflater
     * @param container Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return root
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = FragmentMapBinding.inflate(inflater, container, false).getRoot();

        if (savedInstanceState != null) {
            lastKnownLocation = (Location) savedInstanceState.getParcelable(KEY_LOCATION);
            viewportPosition = savedInstanceState.getParcelable(KEY_VIEWPORT_POSITION);
        }

        db = FirebaseFirestore.getInstance();
        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // this is necessary to get the map to be shown ASAP

        SupportMapFragment mapFrag =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        return root;
    }

    private void setLocation() {
        new FusedLocationProviderClient(this.getActivity())
                .getLastLocation()
                .addOnCompleteListener(
                        locationTask -> {
                            if (locationTask.isSuccessful() && locationTask.getResult() != null) {
                                lastKnownLocation = locationTask.getResult();
                                if (googleMap != null) {
                                    googleMap.moveCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                    new LatLng(
                                                            lastKnownLocation.getLatitude(),
                                                            lastKnownLocation.getLongitude()),
                                                    DEFAULT_ZOOM));
                                }
                            }
                        });
    }
    /**
     * Converts a vector image to a bitmap
     *
     * @param drawable - A vector image
     * @return bitmap of the vector image
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap =
                Bitmap.createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        setLocation();
        Query qrLocation = db.collectionGroup("qrCodes");
        qrLocation
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        if (doc.exists()) {
                                            PlayableQRCode qrCode =
                                                    doc.toObject(PlayableQRCode.class);
                                            if (qrCode.getLocation() != null && qrCode.getLocation().exists()) {
                                                googleMap.addMarker(new MarkerOptions().position(
                                                        new LatLng(qrCode.getLocation().getLatitude(), qrCode.getLocation().getLongitude())));
                                            }
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_VIEWPORT_POSITION, viewportPosition);
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
