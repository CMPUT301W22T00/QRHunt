package com.bigyoshi.qrhunt;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class QrLibraryGridViewAdapter extends ArrayAdapter<PlayableQRCode> {
    private Context context;
    private ArrayList<PlayableQRCode> qrCodes;

    public QrLibraryGridViewAdapter(Context context, ArrayList<PlayableQRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.context = context;
        this.qrCodes = qrCodes;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.qr_grid_element,
                    parent,
                    false);

        }

        PlayableQRCode qrCode = qrCodes.get(position);

        ImageView imageView = view.findViewById(R.id.imageView2);
        //imageView.setImageBitmap();

        Drawable icon = ContextCompat.getDrawable(context,
                R.drawable.ic_baseline_photo_camera_24_light_off_white_blue);

        imageView.setImageDrawable(icon);

        return view;
    }
}
