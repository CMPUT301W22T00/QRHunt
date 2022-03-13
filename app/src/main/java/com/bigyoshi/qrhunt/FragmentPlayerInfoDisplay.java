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

import androidx.fragment.app.DialogFragment;

public class FragmentPlayerInfoDisplay extends DialogFragment {
    private FragmentPlayerInfoDisplay() {   }
    private Contact contact;
    private EditText email,socials;

    public static FragmentPlayerInfoDisplay newInstance(){
        return new FragmentPlayerInfoDisplay();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_display_user_info,null);
        if (getDialog() != null && getDialog().getWindow() != null) {
            email = view.findViewById(R.id.user_email_text);
            email.setText(contact.getEmail());
            socials = view.findViewById(R.id.user_socials_text);
            socials.setText(contact.getSocial());
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

}
