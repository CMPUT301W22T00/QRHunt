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
 * Definition: Creates the grid on player's profile to show the player's QRLibrary
 * Note: N/A
 * Issues: N/A
 */
public class QrLibraryGridViewAdapter extends ArrayAdapter<PlayableQrCode> {
    private Context context;
    private ArrayList<PlayableQrCode> qrCodes;

    /**
     * Constructor method
     *
     * @param context context
     * @param qrCodes list of qr codes
     */
    public QrLibraryGridViewAdapter(Context context, ArrayList<PlayableQrCode> qrCodes) {
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
     *
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

        PlayableQrCode qrCode = qrCodes.get(position);

        // Display image form url or stock image
        ImageView imageView = view.findViewById(R.id.qr_grid_image_view_two);
        if (qrCode.getImageUrl() != null) {
            Picasso.get().load(qrCode.getImageUrl()).into(imageView);
        }
        imageView.setCropToPadding(true); // Crop to the center
        // Display the score under the image
        TextView textView = view.findViewById(R.id.qr_grid_score);
        textView.setText(String.valueOf(qrCode.getScore()));

        return view;
    }
}
