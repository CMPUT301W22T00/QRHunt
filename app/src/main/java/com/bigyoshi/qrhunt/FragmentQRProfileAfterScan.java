package com.bigyoshi.qrhunt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link Fragment} subclass -- Use the {@link FragmentQRProfileAfterScan#newInstance} factory method to
 * create an instance of this fragment
 * Note: NA
 * Issues: TBA
 */
public class FragmentQRProfileAfterScan extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Constructor method (required)
     *
     */
    public FragmentQRProfileAfterScan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1
     * @param param2 Parameter 2
     * @return New instance of fragment QRProfileAfterScan
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentQRProfileAfterScan newInstance(String param1, String param2) {
        FragmentQRProfileAfterScan fragment = new FragmentQRProfileAfterScan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates instance of fragment assigning appropriate params
     *
     * @param savedInstanceState SavedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Creates instance of fragment
     *
     * @param inflater           Inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_profile_after_scan,
                container,
                false);
    }
}