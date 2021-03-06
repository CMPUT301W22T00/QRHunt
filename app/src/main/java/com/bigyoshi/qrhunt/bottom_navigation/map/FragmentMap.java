package com.bigyoshi.qrhunt.bottom_navigation.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentMapBinding;
import com.bigyoshi.qrhunt.qr.PlayableQrCode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.text.DecimalFormat;


/**
 * Definition: Fragment representing the map
 * Note: N/A
 * Issues: N/A
 */
public class FragmentMap extends Fragment {
    private final String TAG = FragmentMap.class.getSimpleName();
    private MapView map = null;
    private FragmentMapBinding binding;
    private MyLocationNewOverlay mLocationOverlay;
    private Marker geoPin;
    private FirebaseFirestore db;
    Double lat;
    Double lng;
    private LocationCallback hackyLocationCallback; // todo rename this maybe
    public FusedLocationProviderClient client;
    public Context ctx;
    Location currentLocation;
    private ImageButton btnRecenter;
    private boolean isInfoWindowShown = false;


    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     *
     * @param inflater           inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return root
     */
    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){

        // Load/Initialize osmdroid configuration, database, and location
        db = FirebaseFirestore.getInstance();
        ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        client = LocationServices.getFusedLocationProviderClient(this.getActivity());
        startPollingLocation();

        // Makes the map load faster via caching
        IConfigurationProvider osmConf = Configuration.getInstance();
        File basePath = new File(this.getActivity().getCacheDir().getAbsolutePath(), "osmdroid");
        basePath.mkdirs();
        osmConf.setOsmdroidBasePath(basePath);
        File tileCache = new File(osmConf.getOsmdroidBasePath().getAbsolutePath(), "tile");
        tileCache.mkdirs();
        osmConf.setOsmdroidTileCache(tileCache);
        osmConf.setUserAgentValue("QRHunt");

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* Inflate and create the map
           setContentView(R.layout.fragment_map); */
        map = binding.mapView;
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Map Zoom Controls
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(20);

        // Creates user location
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);

        // This gets the current user location
        getLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {
                    currentLocation = new Location(location);
                }
                /* this stops listening to the updates that
                we didn't actually care about in the first place for; see startPollingLocation
                 */
                LocationServices.getFusedLocationProviderClient(ctx).removeLocationUpdates(hackyLocationCallback);
            }
        });

        // Change person and directional icon
        Drawable directionIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_player_nav);
        Drawable personIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_icon);
        this.mLocationOverlay.setDirectionArrow(drawableToBitmap(personIcon), drawableToBitmap(directionIcon));

        // Enables and follows user location
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.enableFollowLocation();
        this.mLocationOverlay.setPersonHotspot(30.0f, 30.0f);

        // Recenters the map onto the user
        btnRecenter = (ImageButton) root.findViewById(R.id.map_recenter);
        btnRecenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeoPoint myLocation = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                map.getController().animateTo(myLocation);
                map.getController().setZoom(20);
            }
        });

        // Adding the overlays
        map.getOverlays().add(this.mLocationOverlay);



        Drawable pinIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_qr_pin);
        Query qrLocation =  db.collectionGroup("qrCodes");
        qrLocation.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        if (doc.exists()) {
                            PlayableQrCode qrCode = doc.toObject(PlayableQrCode.class);
                            if (qrCode.getLocation() != null) {
                                lat = qrCode.getLocation().getLatitude();
                                lng = qrCode.getLocation().getLongitude();
                                if (lat != null && lng != null) {
                                    Log.d(TAG, String.format("lat %f long %f", lat, lng));
                                    // Creates marker on map
                                    geoPin = new Marker(map);
                                    geoPin.setPosition(new GeoPoint(lat, lng));
                                    geoPin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                                    // Stuff for distance
                                    Location markerLocation = new Location("");
                                    markerLocation.setLatitude(lat);
                                    markerLocation.setLongitude(lng);
                                    float distance = currentLocation.distanceTo(markerLocation);
                                    String d = formatDistance(distance);
                                    Log.d(TAG, String.format("Distance: %s", d));

                                    // Creates custom info window for the marker
                                    CustomInfoWindow blurb = new CustomInfoWindow(map, d, qrCode);
                                    geoPin.setInfoWindow(blurb);
                                    geoPin.setIcon(pinIcon);

                                    // Shows/Hides info window on click
                                    // From: https://stackoverflow.com/a/45043472
                                    // Author: https://stackoverflow.com/users/6889839/fregomene
                                    geoPin.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                                            if (!isInfoWindowShown){
                                                marker.showInfoWindow();
                                                isInfoWindowShown = true;
                                            }else{
                                                marker.closeInfoWindow();
                                                isInfoWindowShown = false;
                                            }
                                            return true;
                                        }
                                    });

                                    // Adds the pins on the map
                                    map.getOverlays().add(geoPin);
                                }
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


        return root;
    }


    /**
     * Destroys the view and makes binding null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Refreshes the osmdroid configuration on resuming
     */
    @Override
    public void onResume() {
        super.onResume();
        /* if you make changes to the configuration, use:
           SharedPreferences prefs = PreferenceManager
                                         .getDefaultSharedPreferences(this.getContext());
           Configuration.getInstance().load(this.getContext(),
                                          PreferenceManager
                                                .getDefaultSharedPreferences(this.getContext()));
         */
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    /**
     * Pauses the osmdroid configuration when moving to a new fragment/activity
     */
    @Override
    public void onPause() {
        super.onPause();
        /* if you make changes to the configuration, use
           SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
           Configuration.getInstance().save(this.getContext(), prefs); */
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    /**
     * Converts a vector image to a bitmap
     *
     * @param drawable  a vector image
     * @return bitmap   of the vector image
     */
    public static Bitmap drawableToBitmap (Drawable drawable){
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Gets player's geolocation
     * */
    public void startPollingLocation() {

        /* alex please forgive me
        super, super hacky way to get location not to be null.
        apparently just requesting it is enough to get the location manager off it's ass
        even if we never care about the result
        it's super messed up
        if it break in the future, we may need to implement more of LocationCallback
        but it's working now
        https://stackoverflow.com/questions/29441384/fusedlocationapi-getlastlocation-always-null/29854418#29854418
         */

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        hackyLocationCallback = new LocationCallback() {};
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(mLocationRequest, hackyLocationCallback, null);
    }

    /**
     * Gets location if permission is enabled(?) todo is this still a question?
     *
     * @return client.getLastLocation()
     */
    private Task<Location> getLocation() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return client.getLastLocation();
    }

    /**
     * Converts the distance into a string and rounds it to one decimal place
     *
     * @param distance  a float
     * @return d
     */
    public String formatDistance(float distance) {
        String d;
        DecimalFormat value = new DecimalFormat("#.#");
        if (distance >= 1000){
            distance = distance / 1000;
            d = value.format(distance) + "km";
        }
        else{
            d = value.format(distance) + "m";
        }
        return d;
    }

}