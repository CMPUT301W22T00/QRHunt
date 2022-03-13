package com.bigyoshi.qrhunt.bottom_navigation.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentMapBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapFragment extends Fragment {

    private MapView map = null;
    private FragmentMapBinding binding;
    private MyLocationNewOverlay mLocationOverlay;
    //private CompassOverlay mCompassOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Load/Initialize osmdroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inflate and create the map
        //setContentView(R.layout.fragment_map);
        map = (MapView) root.findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Map Zoom Controls
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Follows user and centers on them
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.enableFollowLocation();

        // Change person icon
        Drawable directionIcon = getResources().getDrawable(R.drawable.ic_player_nav, null);
        Drawable personIcon = getResources().getDrawable(R.drawable.ic_person_icon, null);
        this.mLocationOverlay.setDirectionArrow(drawableToBitmap(personIcon), drawableToBitmap(directionIcon));

        // Adding the overlays
        map.getOverlays().add(this.mLocationOverlay);
        IMapController mapController = map.getController();
        mapController.setZoom(20);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
//        Configuration.getInstance().load(this.getContext(), PreferenceManager.getDefaultSharedPreferences(this.getContext()));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
//        Configuration.getInstance().save(this.getContext(), prefs);
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