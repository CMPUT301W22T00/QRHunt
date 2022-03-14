package com.bigyoshi.qrhunt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class FragmentPlayerInfoDisplay extends DialogFragment {
    private TextView email,socials;

    public static FragmentPlayerInfoDisplay newInstance(String email, String social){
        FragmentPlayerInfoDisplay fragmentPlayerInfoDisplay = new FragmentPlayerInfoDisplay();
        Bundle info = new Bundle();
        info.putString("email",email);
        info.putString("social",social);

        fragmentPlayerInfoDisplay.setArguments(info);
        return new FragmentPlayerInfoDisplay();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_display_user_info,null);
        email = view.findViewById(R.id.user_email_text);
        email.setText(getArguments().getString("email"));
        socials = view.findViewById(R.id.user_socials_text);
        socials.setText(getArguments().getString("social"));

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().setCanceledOnTouchOutside(true);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

}
