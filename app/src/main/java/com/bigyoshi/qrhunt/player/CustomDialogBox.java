package com.bigyoshi.qrhunt.player;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bigyoshi.qrhunt.R;

public class CustomDialogBox extends Dialog implements android.view.View.OnClickListener {

    public Context ctx;
    public Button close;
    public String prompt;
    private TextView txtQrGen;
    private ImageView imgQrGen;
    private Bitmap qr;

    public CustomDialogBox(@NonNull Context ctx, String prompt, Bitmap qr) {
        super(ctx);
        this.ctx = ctx;
        this.prompt = prompt;
        this.qr = qr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qr_generation);
        close = (Button) findViewById(R.id.generated_qr_close_button);
        txtQrGen = (TextView) findViewById(R.id.generated_qr_title_text);
        imgQrGen = (ImageView) findViewById(R.id.generated_qr_image);

        txtQrGen.setText(prompt);
        imgQrGen.setImageBitmap(Bitmap.createScaledBitmap(qr, 600, 600, false));
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.generated_qr_close_button){
                dismiss();
        }
        dismiss();
    }
}
