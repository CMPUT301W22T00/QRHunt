package com.bigyoshi.qrhunt.qr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bigyoshi.qrhunt.R;

import java.util.ArrayList;

/**
 * Definition: todo smth smth smth
 * Note: N/A
 * Issues: N/A
 */
public class QRCommentAdapter extends ArrayAdapter<QRComment> {
    private Context context;
    private ArrayList<QRComment> comments;

    /**
     * Constructor method
     *
     * @param context   todo tag
     * @param comments  todo and this...?
     */
    public QRCommentAdapter(Context context, ArrayList<QRComment> comments) {
        super(context, 0, comments);
        this.context = context;
        this.comments = comments;
    }

    /**
     * todo does smth
     *
     * @param position      todo tag
     * @param convertView   todo tag
     * @param parent        todo tag
     *
     * @return view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.qr_comment_element, parent,
                    false);
        }

        QRComment comment = comments.get(position);

        // Displaying the text
        TextView username = view.findViewById(R.id.qr_comment_username);
        username.setText(comment.getUsername());

        TextView commentText = view.findViewById(R.id.qr_comment_text);
        commentText.setText(comment.getComment());

        return view;
    }
}
