package com.bigyoshi.qrhunt.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentHomeBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class HomeFragment extends Fragment {

    private MapView map = null;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        //MapView mMapView = new MapView(inflater.getContext(), 256, getContext());

        //Load/Initialize osmdroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //inflate and create the map
        //setContentView(R.layout.fragment_home);
        map = (MapView) root.findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);

        //Map Zoom Controls
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        //Map Controller stuff to move the map on a default view point
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(53.5461, 113.4938);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}