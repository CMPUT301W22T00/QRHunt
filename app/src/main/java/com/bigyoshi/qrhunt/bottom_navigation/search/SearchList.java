package com.bigyoshi.qrhunt.bottom_navigation.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.player.Player;

import java.util.ArrayList;

public class SearchList extends ArrayAdapter<Player> {
    private ArrayList<Player> playersFound;
    private Context context;

    public SearchList(Context context, ArrayList<Player> playersFound){
        super(context, 0, playersFound);
        this.playersFound = playersFound;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.search_results_content, parent, false);
        }

        Player player = playersFound.get(position);

        TextView username = view.findViewById(R.id.search_results_username_text);
        TextView score = view.findViewById(R.id.search_results_score_text);

        username.setText(player.getUsername());
        score.setText(Integer.toString(player.getTotalScore()));

        return view;
    }
}
