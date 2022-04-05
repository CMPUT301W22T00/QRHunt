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
import com.bigyoshi.qrhunt.player.Player;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Creates the grid on player's profile to show the player's QRLibrary
 * Note: NA
 * Issues: TBA
 */
public class QrLibraryAdapter extends ArrayAdapter<PlayableQrCode> {
    private Context context;
    private ArrayList<PlayableQrCode> qrCodes;
    private Player playerInfo;
    private Player selfPlayer;

    public QrLibraryAdapter(Context context, ArrayList<PlayableQrCode> qrCodes, Player playerInfo, Player selfPlayer) {
        super(context, 0, qrCodes);
        this.context = context;
        this.qrCodes = qrCodes;
        this.selfPlayer = selfPlayer;
        this.playerInfo = playerInfo;
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

        PlayableQrCode qrCode = qrCodes.get(position);

        // Adding flags

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ImageView uniqueFlag = view.findViewById(R.id.qr_grid_unique);
        db.collection("qrCodesMetadata").document(qrCode.getId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().get("numScanned").toString().matches("1")) {
                            uniqueFlag.setVisibility(View.VISIBLE);
                        }
                    }

        if (!selfPlayer.getPlayerId().matches(playerInfo.getPlayerId())) {
            if (selfPlayer.getPlayerId().matches(qrCode.getPlayerId())) {
                ImageView scannedFlag = view.findViewById(R.id.qr_grid_scanned);
                scannedFlag.setVisibility(View.VISIBLE);
            }
        }

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
