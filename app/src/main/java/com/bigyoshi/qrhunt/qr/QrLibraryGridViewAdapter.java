package com.bigyoshi.qrhunt.qr;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bigyoshi.qrhunt.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Description: Creates the grid on player's profile to show the player's QRLibrary
 * Note: NA
 * Issues: TBA
 */
public class QrLibraryGridViewAdapter extends ArrayAdapter<PlayableQRCode> {
    private Context context;
    private ArrayList<PlayableQRCode> qrCodes;

    public QrLibraryGridViewAdapter(Context context, ArrayList<PlayableQRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.context = context;
        this.qrCodes = qrCodes;
    }

    /**
     * Retrieves the view
     *
     * @param position    Position
     * @param convertView View to convert
     * @param parent      Parent view
     * @return View
     */
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

        // Display image form url or stock image
        ImageView imageView = view.findViewById(R.id.imageView2);
        if (qrCode.getImageUrl() != null) {
            Picasso.get().load(qrCode.getImageUrl()).into(imageView);
        } else {
            Drawable icon = ContextCompat.getDrawable(context,
                    R.drawable.ic_baseline_photo_camera_24_light_off_white_blue);
            imageView.setImageDrawable(icon);
        }
        imageView.setCropToPadding(true); // Crop to the center
        // Display the score under the image
        TextView textView = view.findViewById(R.id.show_score_library);
        textView.setText(String.valueOf(qrCode.getScore()));

        return view;
    }
}